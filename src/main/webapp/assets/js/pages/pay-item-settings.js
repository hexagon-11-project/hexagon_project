(function () {
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

  var nonPayAmountInput = document.querySelector('[name="nonPayAmount"]');
  if (nonPayAmountInput) {
    nonPayAmountInput.addEventListener('input', function () {
      var digits = nonPayAmountInput.value.replace(/[^\d]/g, '');
      if (!digits) {
        nonPayAmountInput.value = '';
        return;
      }
      nonPayAmountInput.value = Number(digits).toLocaleString('ko-KR');
    });
    nonPayAmountInput.addEventListener('keypress', function (e) {
      if (e.ctrlKey || e.metaKey || e.key.length !== 1) {
        return;
      }
      if (!/\d/.test(e.key)) {
        e.preventDefault();
      }
    });
  }
})();
