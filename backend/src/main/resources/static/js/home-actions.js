// Navegacao dos CTAs da Home V3, sem alterar seu visual.
(function () {
  const actionPattern = /matricul|inscreva|inscri[cç][aã]o|escolher plano|selecionar plano|assinar|contratar|quero (esse|este|meu) plano|come[cç]ar agora|garantir vaga|seja aluno|fazer parte|aderir/i;
  function findAction(target) {
    return target.closest('a, button, [role="button"], input[type="button"], input[type="submit"]');
  }
  function selectedPlan(element) {
    const card = element.closest('[class*="plano"],[class*="plan"],[class*="pricing"],article,.card');
    if (!card) return '';
    const title = card.querySelector('h2,h3,h4,[class*="title"],[class*="titulo"]');
    return (title?.textContent || '').trim().slice(0,100);
  }
  document.addEventListener('click', function (event) {
    const element = findAction(event.target instanceof Element ? event.target : event.target.parentElement);
    if (!element) return;
    const label = (element.innerText || element.textContent || element.value || element.getAttribute('aria-label') || '').replace(/\s+/g,' ').trim();
    const href = element.getAttribute('href') || '';
    const isEnrollmentLink = /(?:matricul|cadastro|inscri[cç]|signup)/i.test(href);
    if (!actionPattern.test(label) && !isEnrollmentLink) return;
    event.preventDefault();
    event.stopImmediatePropagation();
    const plano = selectedPlan(element);
    window.location.assign('/cadastro' + (plano ? '?plano=' + encodeURIComponent(plano) : ''));
  }, true);
})();
