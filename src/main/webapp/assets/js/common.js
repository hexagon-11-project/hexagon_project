(function(){
  const sidebar=document.querySelector('.sidebar');
  const backdrop=document.querySelector('.sidebar-backdrop');
  document.querySelector('.menu-toggle')?.addEventListener('click',()=>{sidebar?.classList.toggle('open');backdrop?.classList.toggle('open')});
  backdrop?.addEventListener('click',()=>{sidebar?.classList.remove('open');backdrop.classList.remove('open')});
  document.querySelectorAll('[data-tab-target]').forEach(btn=>btn.addEventListener('click',()=>{
    const root=btn.closest('.card, .tab-scope, main')||document;
    root.querySelectorAll('.tab').forEach(x=>x.classList.remove('active'));
    root.querySelectorAll('.tab-panel').forEach(x=>x.classList.remove('active'));
    btn.classList.add('active');document.getElementById(btn.dataset.tabTarget)?.classList.add('active');
  }));
  document.querySelectorAll('[data-check-all]').forEach(box=>box.addEventListener('change',()=>{
    box.closest('table')?.querySelectorAll('tbody input[type=checkbox]').forEach(x=>x.checked=box.checked);
  }));
  document.querySelectorAll('[data-filter-input]').forEach(input=>input.addEventListener('input',()=>{
    const q=input.value.trim().toLowerCase();document.querySelectorAll('#'+input.dataset.filterInput+' tbody tr').forEach(row=>row.hidden=!row.textContent.toLowerCase().includes(q));
  }));
  document.querySelectorAll('[data-select-row]').forEach(row=>row.addEventListener('click',e=>{
    if(e.target.matches('input,button,a')) return;
    row.closest('tbody')?.querySelectorAll('tr').forEach(x=>x.classList.remove('selected'));row.classList.add('selected');
  }));
  document.querySelectorAll('[data-dialog-open]').forEach(btn=>btn.addEventListener('click',()=>document.getElementById(btn.dataset.dialogOpen)?.classList.add('open')));
  document.querySelectorAll('[data-dialog-close]').forEach(btn=>btn.addEventListener('click',()=>btn.closest('.dialog-backdrop')?.classList.remove('open')));
})();
