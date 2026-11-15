// Progressive enhancement: keep original V3 design intact.
document.addEventListener('click', function(event) {
 const el=event.target.closest('a,button');
 if(!el) return;
 const text=(el.textContent||'').replace(/\\s+/g,' ').trim();
 const label=text.toLocaleLowerCase('pt-BR');
 if(!/(matricul|escolher plano|assinar|contratar|quero esse|começar agora|garantir vaga)/i.test(label)) return;
 if(el.closest('nav') && !/matricul/i.test(label)) return;
 event.preventDefault();
 const card=el.closest('[class*="plan"],[class*="plano"],article,.card');
 const heading=card?.querySelector('h2,h3,h4');
 const plano=(heading?.textContent||'').trim().slice(0,100);
 window.location.href='/cadastro'+(plano?'?plano='+encodeURIComponent(plano):'');
},true);
