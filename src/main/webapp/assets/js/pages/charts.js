(function(){
 const palette=['#3f8fc4','#e76c64','#6f7b87','#50a57a','#d19a43','#8d75b8','#58aeb2'];
 function lineChart(canvas){
   const ctx=canvas.getContext('2d'), labels=JSON.parse(canvas.dataset.labels||'[]'), series=JSON.parse(canvas.dataset.series||'[]');
   const w=canvas.width=canvas.clientWidth*devicePixelRatio,h=canvas.height=300*devicePixelRatio;ctx.scale(devicePixelRatio,devicePixelRatio);const W=canvas.clientWidth,H=300,p={l:54,r:18,t:20,b:38};
   ctx.clearRect(0,0,W,H);ctx.strokeStyle='#dfe5eb';ctx.fillStyle='#6f7c8b';ctx.font='11px sans-serif';
   for(let i=0;i<=5;i++){const y=p.t+(H-p.t-p.b)*i/5;ctx.beginPath();ctx.moveTo(p.l,y);ctx.lineTo(W-p.r,y);ctx.stroke();}
   labels.forEach((x,i)=>{const px=p.l+(W-p.l-p.r)*i/Math.max(labels.length-1,1);ctx.fillText(x,px-12,H-14)});
   const max=Math.max(...series.flatMap(s=>s.values),1)*1.12;
   series.forEach((s,si)=>{ctx.strokeStyle=palette[si];ctx.lineWidth=2;ctx.beginPath();s.values.forEach((v,i)=>{const x=p.l+(W-p.l-p.r)*i/Math.max(labels.length-1,1),y=H-p.b-(H-p.t-p.b)*v/max;i?ctx.lineTo(x,y):ctx.moveTo(x,y)});ctx.stroke();});
 }
 function donut(canvas){
   const ctx=canvas.getContext('2d'), values=JSON.parse(canvas.dataset.values||'[]');const W=canvas.width=canvas.clientWidth*devicePixelRatio,H=canvas.height=300*devicePixelRatio;ctx.scale(devicePixelRatio,devicePixelRatio);const cx=W/devicePixelRatio/2,cy=150,r=105,total=values.reduce((a,b)=>a+b,0)||1;let start=-Math.PI/2;
   values.forEach((v,i)=>{const end=start+Math.PI*2*v/total;ctx.beginPath();ctx.moveTo(cx,cy);ctx.arc(cx,cy,r,start,end);ctx.closePath();ctx.fillStyle=palette[i%palette.length];ctx.fill();start=end});ctx.beginPath();ctx.arc(cx,cy,55,0,Math.PI*2);ctx.fillStyle='#fff';ctx.fill();ctx.fillStyle='#263445';ctx.font='700 15px sans-serif';ctx.textAlign='center';ctx.fillText('지급항목',cx,cy+5);
 }
 document.querySelectorAll('canvas[data-chart=line]').forEach(lineChart);document.querySelectorAll('canvas[data-chart=donut]').forEach(donut);
 addEventListener('resize',()=>{document.querySelectorAll('canvas[data-chart=line]').forEach(lineChart);document.querySelectorAll('canvas[data-chart=donut]').forEach(donut)});
})();
