// 급여항목 설정 화면의 클라이언트 동작을 묶는다
// 給与項目設定画面のクライアント動作をまとめる。
(function () {
  // 지급항목 폼에서 자주 쓰는 노드를 미리 찾는다
  // 支給項目フォームでよく使うノードを先に探す。
  var attendanceSelect = document.querySelector('[name="attendancePayRule"]');
  var bulkPayRow = document.getElementById('bulkPayAmountRow');
  var taxableRadios = document.querySelectorAll('[name="taxableYn"]');
  var taxableWrap = document.querySelector('[data-nontax-popup-url]');
  var nonTaxPopup = null;

  // 비과세 관련 input 을 한 번에 찾는다
  // 非課税関連の input を一度に探す。
  function getNonTaxInputs() {
    // 항목명·한도·숨은 PK 를 객체로 돌려 준다
    // 項目名・限度・隠しPKをオブジェクトで返す。
    return {
      categoryInput: document.querySelector('[name="nonTaxCategory"]'),
      amountInput: document.querySelector('[name="nonPayAmount"]'),
      nonTaxIdInput: document.querySelector('[name="nonTaxId"]')
    };
  }

  // 일괄지급일 때만 금액 행을 보여 준다
  // 一括支給のときだけ金額行を表示する。
  function toggleBulkPayAmountRow() {
    // 셀렉트나 행이 없으면 중단한다
    // セレクトや行がなければ中断する。
    if (!attendanceSelect || !bulkPayRow) {
      return;
    }
    // 일본어 라벨이 아니라 value 한국어 일괄지급 과 비교한다
    // 日本語ラベルではなく value の韓国語 일괄지급 と比較する。
    var isBulk = attendanceSelect.value === '일괄지급';
    // 일괄지급이면 display 를 비워 CSS 기본을 쓰고, 아니면 none 이다
    // 一括支給なら display を空にしてCSS既定を使い、そうでなければ none である。
    bulkPayRow.style.display = isBulk ? '' : 'none';
    if (!isBulk) {
      // 숨긴 행의 금액을 지운다
      // 隠した行の金額を消す。
      var bulkInput = bulkPayRow.querySelector('[name="bulkPayAmount"]');
      if (bulkInput) {
        bulkInput.value = '';
      }
    }
  }

  // 비과세 상세 팝업을 연다
  // 非課税詳細ポップアップを開く。
  function openNonTaxDetailPopup() {
    // 과세 라디오를 감싼 div 가 없으면 중단한다
    // 課税ラジオを包んだ div がなければ中断する。
    if (!taxableWrap) {
      return;
    }
    var popupUrl = taxableWrap.getAttribute('data-nontax-popup-url');
    if (!popupUrl) {
      return;
    }
    // 팝업 크기와 위치를 화면 가운데로 잡는다
    // ポップアップの大きさと位置を画面中央にする。
    var width = 960;
    var height = 560;
    var left = Math.max(0, (window.screen.width - width) / 2);
    var top = Math.max(0, (window.screen.height - height) / 2);
    var features = 'width=' + width + ',height=' + height + ',left=' + left + ',top=' + top
      + ',scrollbars=yes,resizable=yes';

    // 기존 팝업이 살아 있으면 포커스만 옮긴다
    // 既存ポップアップが生きていればフォーカスだけ移す。
    if (nonTaxPopup && !nonTaxPopup.closed) {
      nonTaxPopup.focus();
      nonTaxPopup.location.href = popupUrl;
      return;
    }
    // 새 창 이름은 nonTaxDetailPopup 으로 고정한다
    // 新しいウィンドウ名は nonTaxDetailPopup に固定する。
    nonTaxPopup = window.open(popupUrl, 'nonTaxDetailPopup', features);
  }

  // 팝업에서 고른 비과세 값을 폼에 넣는다
  // ポップアップで選んだ非課税値をフォームに入れる。
  window.applyNonTaxDetail = function (detail) {
    // 전달 객체가 없으면 아무 것도 안 한다
    // 渡されたオブジェクトがなければ何もしない。
    if (!detail) {
      return;
    }
    var inputs = getNonTaxInputs();
    // 非課税項目名 칸에 카테고리를 넣는다
    // 非課税項目名欄にカテゴリを入れる。
    if (inputs.categoryInput) {
      inputs.categoryInput.value = detail.nonTaxCategory || '';
    }
    // 非課税限度額 칸에 한도 라벨을 넣는다
    // 非課税限度額欄に限度ラベルを入れる。
    if (inputs.amountInput) {
      inputs.amountInput.value = detail.limitAmountLabel || '';
    }
    // hidden nonTaxId 에 목록 PK 를 넣는다
    // hidden nonTaxId に一覧PKを入れる。
    if (inputs.nonTaxIdInput) {
      inputs.nonTaxIdInput.value = detail.nonTaxId || '';
    }
  };

  // 비과세 항목명·한도를 손으로 입력하게 한다
  // 非課税の項目名・限度を手入力できるようにする。
  window.enableManualNonTaxInput = function () {
    var inputs = getNonTaxInputs();
    // 목록에서 고른 PK 를 지운다
    // 一覧から選んだPKを消す。
    if (inputs.nonTaxIdInput) {
      inputs.nonTaxIdInput.value = '';
    }
    // 항목명을 비우고 키보드 입력을 연다
    // 項目名を空にしてキーボード入力を開く。
    if (inputs.categoryInput) {
      inputs.categoryInput.value = '';
      inputs.categoryInput.readOnly = false;
      inputs.categoryInput.focus();
    }
    // 한도 금액도 비우고 입력을 연다
    // 限度金額も空にして入力を開く。
    if (inputs.amountInput) {
      inputs.amountInput.value = '';
      inputs.amountInput.readOnly = false;
    }
  };

  // 근태 셀렉트 변경과 첫 화면 표시를 맞춘다
  // 勤怠セレクトの変更と初画面表示を合わせる。
  if (attendanceSelect) {
    attendanceSelect.addEventListener('change', toggleBulkPayAmountRow);
    toggleBulkPayAmountRow();
  }

  // 비과세 라디오를 누를 때마다 팝업을 연다
  // 非課税ラジオを押すたびにポップアップを開く。
  taxableRadios.forEach(function (radio) {
    // Y=全体課税 에는 리스너를 달지 않는다
    // Y=全体課税 にはリスナーを付けない。
    if (radio.value !== 'N') {
      return;
    }
    radio.addEventListener('click', function () {
      // 非課税 클릭이 팝업 트리거이다
      // 非課税クリックがポップアップのトリガーである。
      openNonTaxDetailPopup();
    });
  });

  var nonPayAmountInput = document.querySelector('[name="nonPayAmount"]');
  if (nonPayAmountInput) {
    // 숫자만 남기고 천 단위 콤마를 붙인다
    // 数字だけ残して千単位のカンマを付ける。
    nonPayAmountInput.addEventListener('input', function () {
      // 숫자가 아닌 문자를 모두 뺀다
      // 数字でない文字をすべて除く。
      var digits = nonPayAmountInput.value.replace(/[^\d]/g, '');
      if (!digits) {
        nonPayAmountInput.value = '';
        return;
      }
      // ko-KR 로 1,000 형태를 만든다
      // ko-KR で 1,000 形式を作る。
      nonPayAmountInput.value = Number(digits).toLocaleString('ko-KR');
    });
    // 숫자 키만 통과시킨다
    // 数字キーだけ通す。
    nonPayAmountInput.addEventListener('keypress', function (e) {
      // 복사·붙여넣기·Backspace 는 막지 않는다
      // コピー・貼り付け・Backspace は妨げない。
      if (e.ctrlKey || e.metaKey || e.key.length !== 1) {
        return;
      }
      // 숫자 한 글자가 아니면 입력을 취소한다
      // 数字一文字でなければ入力を取り消す。
      if (!/\d/.test(e.key)) {
        e.preventDefault();
      }
    });
  }
})();
