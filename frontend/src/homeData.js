export const defaultHome = {
  banner: { title: 'TREINE NO SEU RITMO. CONQUISTE SEU IMPÉRIO.', description: 'Estrutura, orientação e um ambiente feito para você evoluir todos os dias.', button: 'Matricule-se agora', target: '#planos', active: true, image: '' },
  about: { title: 'SOBRE A IMPÉRIO ACADEMIA', description: 'Um espaço pensado para quem leva o treino a sério e também para quem está começando. Aqui você encontra acolhimento, orientação e estrutura para alcançar seus objetivos.', image: '', features: [
    { title: 'Equipamentos', description: 'Aparelhos e áreas para todos os tipos de treino.', active: true },
    { title: 'Profissionais', description: 'Equipe pronta para orientar o seu treino.', active: true },
    { title: 'Ambiente', description: 'Espaço organizado e confortável para treinar.', active: true },
  ] },
  plans: [
    { id: 1, name: 'PLANO MENSAL', description: 'Para quem quer começar sem compromisso longo.', price: '99', period: '/mês', benefits: ['Acesso a toda a estrutura', 'Avaliação física inicial', 'Horário livre'], active: true, featured: false, link: '' },
    { id: 2, name: 'PLANO TRIMESTRAL', description: 'Mais economia para manter a rotina por três meses.', price: '89', period: '/mês', benefits: ['Acesso a toda a estrutura', 'Avaliação física inicial', 'Horário livre'], active: true, featured: true, link: '' },
    { id: 3, name: 'PLANO ANUAL', description: 'O melhor custo mensal para quem treina o ano todo.', price: '79', period: '/mês', benefits: ['Acesso a toda a estrutura', 'Avaliação física inicial', 'Horário livre'], active: true, featured: false, link: '' },
  ],
  gallery: Array.from({ length: 6 }, (_, i) => ({ id: i + 1, caption: 'Foto ' + (i + 1), visible: true, image: '' })),
  contact: { whatsapp: '(91) 90000-0000', waLink: '', instagram: '@imperioacademia', igLink: '', address: 'Rua Exemplo, 123 · Centro', hours: 'Seg a sex · 06h às 22h\nSáb · 08h às 14h' },
}
const copy = (v) => JSON.parse(JSON.stringify(v))
export const loadPublished = () => {
  try { return { ...copy(defaultHome), ...JSON.parse(localStorage.getItem('imperio_published') || '{}') } }
  catch { return copy(defaultHome) }
}
export const loadDraft = () => {
  try { return JSON.parse(localStorage.getItem('imperio_draft')) || loadPublished() }
  catch { return loadPublished() }
}
export const saveDraft = (data) => localStorage.setItem('imperio_draft', JSON.stringify(data))
export const publishHome = (data) => {
  localStorage.setItem('imperio_published', JSON.stringify(data))
  window.dispatchEvent(new Event('imperio-home-updated'))
}
