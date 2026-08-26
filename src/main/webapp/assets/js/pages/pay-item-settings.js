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

  function getNonTaxInputs() {
    return {
      categoryInput: document.querySelector('[name="nonTaxCategory"]'),
      amountInput: document.querySelector('[name="nonPayAmount"]'),
      nonTaxIdInput: document.querySelector('[name="nonTaxId"]')
    };
  }

  function toggleBulkPayAmountRow() {
    if (!attendanceSelect || !bulkPayRow) {
      return;
    }
    var isBulk = attendanceSelect.value === '일괄지급';
    bulkPayRow.style.display = isBulk ? '' : 'none';
    if (!isBulk) {
      var bulkInput = bulkPayRow.querySelector('[name="bulkPayAmount"]');
      if (bulkInput) {
        bulkInput.value = '';
      }
    }
  }

  function openNonTaxDetailPopup() {
    if (!taxableWrap) {
      return;
    }
    var popupUrl = taxableWrap.getAttribute('data-nontax-popup-url');
    if (!popupUrl) {
      return;
    }
    var width = 960;
    var height = 560;
    var left = Math.max(0, (window.screen.width - width) / 2);
    var top = Math.max(0, (window.screen.height - height) / 2);
    var features = 'width=' + width + ',height=' + height + ',left=' + left + ',top=' + top
      + ',scrollbars=yes,resizable=yes';

    if (nonTaxPopup && !nonTaxPopup.closed) {
      nonTaxPopup.focus();
      nonTaxPopup.location.href = popupUrl;
      return;
    }
    nonTaxPopup = window.open(popupUrl, 'nonTaxDetailPopup', features);
    if (!nonTaxPopup) {
      alert('ポップアップがブロックされました。ブラウザの設定を確認してください。');
    }
  }

  window.applyNonTaxDetail = function (detail) {
    if (!detail) {
      return;
    }
    var inputs = getNonTaxInputs();
    if (inputs.categoryInput) {
      inputs.categoryInput.value = detail.nonTaxCategory || '';
    }
    if (inputs.amountInput) {
      inputs.amountInput.value = detail.limitAmountLabel || '';
    }
    if (inputs.nonTaxIdInput) {
      inputs.nonTaxIdInput.value = detail.nonTaxId || '';
    }
  };

  window.enableManualNonTaxInput = function () {
    var inputs = getNonTaxInputs();
    if (inputs.nonTaxIdInput) {
      inputs.nonTaxIdInput.value = '';
    }
    if (inputs.categoryInput) {
      inputs.categoryInput.value = '';
      inputs.categoryInput.readOnly = false;
      inputs.categoryInput.focus();
    }
    if (inputs.amountInput) {
      inputs.amountInput.value = '';
      inputs.amountInput.readOnly = false;
    }
  };

  function isNumericAmount(value) {
    var stripped = String(value).replace(/[,\s]/g, '');
    return /^\d+$/.test(stripped);
  }

  function bindAmountInput(input) {
    if (!input) {
      return;
    }
    input.addEventListener('input', function () {
      var digits = input.value.replace(/[^\d]/g, '');
      if (!digits) {
        input.value = '';
        return;
      }
      input.value = Number(digits).toLocaleString('ko-KR');
    });
    input.addEventListener('keypress', function (e) {
      if (e.ctrlKey || e.metaKey || e.key.length !== 1) {
        return;
      }
      if (!/\d/.test(e.key)) {
        e.preventDefault();
      }
    });
  }

  window.validatePayItemSubmit = function (needId) {
    var form = document.getElementById('payItemForm');
    if (!form) {
      return false;
    }
    var idInput = form.querySelector('[name="payItemId"]');
    var nameInput = form.querySelector('[name="payItemName"]');
    if (needId && (!idInput || !idInput.value)) {
      alert('修正する項目をリストから選択してください。');
      return false;
    }
    if (!nameInput || !nameInput.value.trim()) {
      alert('支給項目を入力してください。');
      return false;
    }
    var taxableN = form.querySelector('[name="taxableYn"][value="N"]');
    if (taxableN && taxableN.checked) {
      var categoryInput = form.querySelector('[name="nonTaxCategory"]');
      var amountInput = form.querySelector('[name="nonPayAmount"]');
      if (!categoryInput || !categoryInput.value.trim()) {
        alert('非課税項目名を入力してください。');
        return false;
      }
      if (!amountInput || !amountInput.value.trim()) {
        alert('非課税限度額を入力してください。');
        return false;
      }
      if (!isNumericAmount(amountInput.value)) {
        alert('金額は数字で入力してください。');
        return false;
      }
    }
    var ruleSelect = form.querySelector('[name="attendancePayRule"]');
    if (ruleSelect && ruleSelect.value === '일괄지급') {
      var bulkInput = form.querySelector('[name="bulkPayAmount"]');
      if (!bulkInput || !bulkInput.value.trim()) {
        alert('一括支給額を入力してください。');
        return false;
      }
      if (!isNumericAmount(bulkInput.value)) {
        alert('金額は数字で入力してください。');
        return false;
      }
    }
    var nonPayInput = form.querySelector('[name="nonPayAmount"]');
    if (nonPayInput && nonPayInput.value.trim() && !isNumericAmount(nonPayInput.value)) {
      alert('金額は数字で入力してください。');
      return false;
    }
    return true;
  };

  window.validatePayItemDelete = function () {
    var form = document.getElementById('payItemForm');
    var idInput = form ? form.querySelector('[name="payItemId"]') : null;
    if (!idInput || !idInput.value) {
      alert('削除する項目をリストから選択してください。');
      return false;
    }
    return confirm('選択した支給項目を削除しますか？');
  };

  window.clearPayItemForm = function (url) {
    if (!confirm('入力中の内容を破棄しますか？')) {
      return;
    }
    location.href = url;
  };

  window.validateDeductionItemSubmit = function (needId) {
    var form = document.getElementById('deductionItemForm');
    if (!form) {
      return false;
    }
    var idInput = form.querySelector('[name="deductionItemId"]');
    var nameInput = form.querySelector('[name="deductionItemName"]');
    if (needId && (!idInput || !idInput.value)) {
      alert('修正する項目をリストから選択してください。');
      return false;
    }
    if (!nameInput || !nameInput.value.trim()) {
      alert('控除項目を入力してください。');
      return false;
    }
    return true;
  };

  window.validateDeductionItemDelete = function () {
    var form = document.getElementById('deductionItemForm');
    var idInput = form ? form.querySelector('[name="deductionItemId"]') : null;
    if (!idInput || !idInput.value) {
      alert('削除する項目をリストから選択してください。');
      return false;
    }
    return confirm('選択した控除項目を削除しますか？');
  };

  window.clearDeductionItemForm = function (url) {
    if (!confirm('入力中の内容を破棄しますか？')) {
      return;
    }
    location.href = url;
  };

  if (attendanceSelect) {
    attendanceSelect.addEventListener('change', toggleBulkPayAmountRow);
    toggleBulkPayAmountRow();
  }

  taxableRadios.forEach(function (radio) {
    if (radio.value !== 'N') {
      return;
    }
    radio.addEventListener('click', function () {
      openNonTaxDetailPopup();
    });
  });

  bindAmountInput(document.querySelector('[name="nonPayAmount"]'));
  bindAmountInput(document.querySelector('[name="bulkPayAmount"]'));

  var errorInput = document.getElementById('payItemSetErrorMessage');
  if (errorInput && errorInput.value) {
    alert(errorInput.value);
  }
})();
