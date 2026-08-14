(function () {
	const palette = ['#3f8fc4', '#e76c64', '#6f7b87', '#50a57a', '#d19a43', '#8d75b8', '#58aeb2'];
	const COMBO_BAR = '#A9D08E';
	const COMBO_LINE = '#ED7D31';
	const COMBO_LINE_LABEL = '#5B9BD5';

	function dpr() {
		return window.devicePixelRatio || 1;
	}

	function lineChart(canvas) {
		const ctx = canvas.getContext('2d');
		const labels = JSON.parse(canvas.getAttribute('data-labels') || '[]');
		const series = JSON.parse(canvas.getAttribute('data-series') || '[]');
		const ratio = dpr();
		const W = Math.max(canvas.clientWidth || canvas.parentElement.clientWidth || 600, 300);
		const H = 300;
		canvas.width = W * ratio;
		canvas.height = H * ratio;
		ctx.setTransform(ratio, 0, 0, ratio, 0, 0);
		const p = { l: 54, r: 18, t: 20, b: 38 };
		ctx.clearRect(0, 0, W, H);
		ctx.strokeStyle = '#dfe5eb';
		ctx.fillStyle = '#6f7c8b';
		ctx.font = '11px sans-serif';
		for (let i = 0; i <= 5; i++) {
			const y = p.t + (H - p.t - p.b) * i / 5;
			ctx.beginPath();
			ctx.moveTo(p.l, y);
			ctx.lineTo(W - p.r, y);
			ctx.stroke();
		}
		labels.forEach((x, i) => {
			const px = p.l + (W - p.l - p.r) * i / Math.max(labels.length - 1, 1);
			ctx.fillText(x, px - 12, H - 14);
		});
		const values = series.flatMap(s => s.values || []);
		const max = Math.max.apply(null, values.concat([1])) * 1.12;
		series.forEach((s, si) => {
			ctx.strokeStyle = palette[si];
			ctx.lineWidth = 2;
			ctx.beginPath();
			(s.values || []).forEach((v, i) => {
				const x = p.l + (W - p.l - p.r) * i / Math.max(labels.length - 1, 1);
				const y = H - p.b - (H - p.t - p.b) * v / max;
				i ? ctx.lineTo(x, y) : ctx.moveTo(x, y);
			});
			ctx.stroke();
		});
	}

	function donut(canvas) {
		const ctx = canvas.getContext('2d');
		const values = JSON.parse(canvas.getAttribute('data-values') || '[]');
		const ratio = dpr();
		const cssW = Math.max(canvas.clientWidth || canvas.parentElement.clientWidth || 300, 300);
		canvas.width = cssW * ratio;
		canvas.height = 300 * ratio;
		ctx.setTransform(ratio, 0, 0, ratio, 0, 0);
		const cx = cssW / 2, cy = 150, r = 105;
		const total = values.reduce((a, b) => a + b, 0) || 1;
		let start = -Math.PI / 2;
		values.forEach((v, i) => {
			const end = start + Math.PI * 2 * v / total;
			ctx.beginPath();
			ctx.moveTo(cx, cy);
			ctx.arc(cx, cy, r, start, end);
			ctx.closePath();
			ctx.fillStyle = palette[i % palette.length];
			ctx.fill();
			start = end;
		});
		ctx.beginPath();
		ctx.arc(cx, cy, 55, 0, Math.PI * 2);
		ctx.fillStyle = '#fff';
		ctx.fill();
		ctx.fillStyle = '#263445';
		ctx.font = '700 15px sans-serif';
		ctx.textAlign = 'center';
		ctx.fillText('지급항목', cx, cy + 5);
	}

	function formatNumber(n) {
		return Math.round(n).toLocaleString('ko-KR');
	}

	function niceMax(value, steps) {
		if (!isFinite(value) || value <= 0) return steps;
		const raw = value * 1.12;
		const magnitude = Math.pow(10, Math.floor(Math.log10(raw)));
		const normalized = raw / magnitude;
		let nice;
		if (normalized <= 1) nice = 1;
		else if (normalized <= 2) nice = 2;
		else if (normalized <= 5) nice = 5;
		else nice = 10;
		const candidate = nice * magnitude;
		const step = candidate / steps;
		return Math.ceil(raw / step) * step;
	}

	function readComboData(canvas) {
		const dataId = canvas.getAttribute('data-source');
		if (dataId && window[dataId]) {
			return window[dataId];
		}
		if (dataId) {
			const el = document.getElementById(dataId);
			if (el && el.textContent) {
				return JSON.parse(el.textContent);
			}
		}
		return {
			labels: JSON.parse(canvas.getAttribute('data-labels') || '[]'),
			bar: JSON.parse(canvas.getAttribute('data-bar') || '{}'),
			line: JSON.parse(canvas.getAttribute('data-line') || '{}')
		};
	}

	/** 막대(좌측 축) + 라인(우측 축) 복합 차트 */
	function comboChart(canvas) {
		const ctx = canvas.getContext('2d');
		if (!ctx) return;

		let data;
		try {
			data = readComboData(canvas);
		} catch (e) {
			console.error('combo chart data parse error', e);
			return;
		}

		const labels = data.labels || [];
		const bar = data.bar || {};
		const line = data.line || {};
		const barValues = bar.values || [];
		const lineValues = line.values || [];
		const barName = bar.name || '전체 급여액 (천원)';
		const lineName = line.name || '인원 (명)';

		const ratio = dpr();
		const cssW = Math.max(canvas.clientWidth || (canvas.parentElement && canvas.parentElement.clientWidth) || 0, 320);
		const cssH = 400;
		canvas.width = cssW * ratio;
		canvas.height = cssH * ratio;
		canvas.style.width = '100%';
		canvas.style.height = cssH + 'px';
		ctx.setTransform(ratio, 0, 0, ratio, 0, 0);

		const p = { l: 72, r: 56, t: 28, b: 72 };
		const plotW = cssW - p.l - p.r;
		const plotH = cssH - p.t - p.b;
		const n = Math.max(labels.length, 1);
		const slot = plotW / n;
		const barW = Math.min(42, slot * 0.55);

		const barMaxValue = barValues.length ? Math.max.apply(null, barValues) : 0;
		const lineMaxValue = lineValues.length ? Math.max.apply(null, lineValues) : 0;
		const barMax = niceMax(barMaxValue, 7);
		const lineMax = niceMax(lineMaxValue, 2);

		ctx.clearRect(0, 0, cssW, cssH);
		ctx.fillStyle = '#fff';
		ctx.fillRect(0, 0, cssW, cssH);

		ctx.strokeStyle = '#b8c0c8';
		ctx.lineWidth = 1;
		ctx.beginPath();
		ctx.moveTo(p.l, p.t);
		ctx.lineTo(p.l, p.t + plotH);
		ctx.lineTo(p.l + plotW, p.t + plotH);
		ctx.lineTo(p.l + plotW, p.t);
		ctx.stroke();

		ctx.fillStyle = '#5f6b77';
		ctx.font = '11px sans-serif';
		ctx.textAlign = 'right';
		ctx.textBaseline = 'middle';
		for (let i = 0; i <= 7; i++) {
			const ratioY = i / 7;
			const y = p.t + plotH - plotH * ratioY;
			ctx.fillText(formatNumber(barMax * ratioY), p.l - 8, y);
			ctx.beginPath();
			ctx.moveTo(p.l - 4, y);
			ctx.lineTo(p.l, y);
			ctx.strokeStyle = '#b8c0c8';
			ctx.stroke();
		}

		ctx.textAlign = 'left';
		for (let i = 0; i <= 2; i++) {
			const ratioY = i / 2;
			const y = p.t + plotH - plotH * ratioY;
			ctx.fillText(String(Math.round(lineMax * ratioY * 10) / 10), p.l + plotW + 8, y);
			ctx.beginPath();
			ctx.moveTo(p.l + plotW, y);
			ctx.lineTo(p.l + plotW + 4, y);
			ctx.stroke();
		}

		ctx.save();
		ctx.translate(16, p.t + plotH / 2);
		ctx.rotate(-Math.PI / 2);
		ctx.textAlign = 'center';
		ctx.textBaseline = 'middle';
		ctx.fillStyle = '#44505c';
		ctx.font = '12px sans-serif';
		ctx.fillText(barName, 0, 0);
		ctx.restore();

		ctx.save();
		ctx.translate(cssW - 14, p.t + plotH / 2);
		ctx.rotate(Math.PI / 2);
		ctx.textAlign = 'center';
		ctx.textBaseline = 'middle';
		ctx.fillText(lineName, 0, 0);
		ctx.restore();

		barValues.forEach((v, i) => {
			const cx = p.l + slot * i + slot / 2;
			const bh = plotH * (v / (barMax || 1));
			const x = cx - barW / 2;
			const y = p.t + plotH - bh;
			ctx.fillStyle = COMBO_BAR;
			ctx.fillRect(x, y, barW, Math.max(bh, 0));

			if (v > 0 && bh > 28) {
				ctx.save();
				ctx.translate(cx, y + 10);
				ctx.rotate(-Math.PI / 2);
				ctx.fillStyle = '#2f3a45';
				ctx.font = '11px sans-serif';
				ctx.textAlign = 'right';
				ctx.textBaseline = 'middle';
				ctx.fillText(formatNumber(v), 0, 0);
				ctx.restore();
			} else if (v > 0) {
				ctx.fillStyle = '#2f3a45';
				ctx.font = '11px sans-serif';
				ctx.textAlign = 'center';
				ctx.textBaseline = 'bottom';
				ctx.fillText(formatNumber(v), cx, y - 4);
			}
		});

		ctx.strokeStyle = COMBO_LINE;
		ctx.lineWidth = 2;
		ctx.beginPath();
		lineValues.forEach((v, i) => {
			const x = p.l + slot * i + slot / 2;
			const y = p.t + plotH - plotH * (v / (lineMax || 1));
			i ? ctx.lineTo(x, y) : ctx.moveTo(x, y);
		});
		ctx.stroke();

		lineValues.forEach((v, i) => {
			const x = p.l + slot * i + slot / 2;
			const y = p.t + plotH - plotH * (v / (lineMax || 1));
			ctx.beginPath();
			ctx.arc(x, y, 4.5, 0, Math.PI * 2);
			ctx.fillStyle = COMBO_LINE;
			ctx.fill();
			ctx.strokeStyle = '#fff';
			ctx.lineWidth = 1.5;
			ctx.stroke();

			ctx.fillStyle = COMBO_LINE_LABEL;
			ctx.font = '12px sans-serif';
			ctx.textAlign = 'center';
			ctx.textBaseline = 'bottom';
			const label = Number.isInteger(v) ? String(v) : String(Math.round(v * 10) / 10);
			ctx.fillText(label, x, y - 8);
		});

		ctx.fillStyle = '#5f6b77';
		ctx.font = '11px sans-serif';
		ctx.textAlign = 'center';
		ctx.textBaseline = 'top';
		labels.forEach((label, i) => {
			const x = p.l + slot * i + slot / 2;
			ctx.fillText(label, x, p.t + plotH + 10);
		});

		const legendY = cssH - 22;
		const items = [
			{ color: COMBO_BAR, text: barName },
			{ color: COMBO_LINE, text: lineName }
		];
		ctx.font = '12px sans-serif';
		const legendWidth = items.reduce((sum, item) => sum + 18 + ctx.measureText(item.text).width + 24, 0);
		let lx = (cssW - legendWidth) / 2;
		items.forEach(item => {
			ctx.fillStyle = item.color;
			ctx.fillRect(lx, legendY - 7, 12, 12);
			ctx.fillStyle = '#44505c';
			ctx.textAlign = 'left';
			ctx.textBaseline = 'middle';
			ctx.fillText(item.text, lx + 18, legendY);
			lx += 18 + ctx.measureText(item.text).width + 24;
		});
	}

	function renderAll() {
		document.querySelectorAll('canvas[data-chart=line]').forEach(lineChart);
		document.querySelectorAll('canvas[data-chart=donut]').forEach(donut);
		document.querySelectorAll('canvas[data-chart=combo]').forEach(function (canvas) {
			try {
				comboChart(canvas);
			} catch (e) {
				console.error('combo chart render error', e);
			}
		});
	}

	function scheduleRender() {
		requestAnimationFrame(function () {
			requestAnimationFrame(renderAll);
		});
	}

	if (document.readyState === 'loading') {
		document.addEventListener('DOMContentLoaded', scheduleRender);
	} else {
		scheduleRender();
	}
	window.addEventListener('resize', scheduleRender);
})();
