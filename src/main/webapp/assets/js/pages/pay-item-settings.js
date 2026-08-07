(function () {
  var attendanceSelect = document.querySelector('[name="attendancePayRule"]');
  var bulkPayRow = document.getElementById('bulkPayAmountRow');

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

  if (attendanceSelect) {
    attendanceSelect.addEventListener('change', toggleBulkPayAmountRow);
    toggleBulkPayAmountRow();
  }
})();
