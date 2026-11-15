import { useEffect, useState } from 'react'
import { defaultHome, loadPublished, loadDraft, saveDraft, publishHome } from './homeData'
import './App.css'

function ExternalButton({ url, children, className = 'gold-button' }) {
  return <a className={className} href={/^https?:\/\//i.test(url || '') ? url : '#contato'} target={/^https?:\/\//i.test(url || '') ? '_blank' : undefined} rel="noreferrer">{children}</a>
}
function Home({ data }) {
  const [menu, setMenu] = useState(false)
  const { banner, about, plans, gallery, contact } = data
  const wa = contact.waLink || (contact.whatsapp.replace(/\D/g, '').length >= 10 ? 'https://wa.me/55' + contact.whatsapp.replace(/\D/g, '') : '')
  const ig = contact.igLink || (contact.instagram.startsWith('@') ? 'https://instagram.com/' + contact.instagram.slice(1) : '')
  return <>
    <header className="topbar"><div className="container nav-inner">
      <a className="brand" href="#inicio"><span className="brand-mark">IF</span><span>IMPÉRIO <strong>FITNESS</strong><small>ACADEMIA</small></span></a>
      <button className="menu-button" onClick={() => setMenu(!menu)} aria-label="Abrir menu">☰</button>
      <nav className={menu ? 'navlinks open' : 'navlinks'} onClick={() => setMenu(false)}>
        <a href="#sobre">A academia</a><a href="#planos">Planos</a><a href="#galeria">Galeria</a><a href="#contato">Contato</a>
        <a className="gold-button nav-cta" href="#planos">Matricule-se →</a>
      </nav>
    </div></header>
    <main>
      <section id="inicio" className="hero" style={banner.image ? { backgroundImage: 'linear-gradient(90deg,#08090bf5,#08090b70),url(' + JSON.stringify(banner.image) + ')' } : undefined}>
        <div className="container hero-inner">{banner.active && <div className="hero-copy">
          <span className="eyebrow">IMPÉRIO ACADEMIA</span><h1>{banner.title}</h1><p>{banner.description}</p>
          <a className="gold-button" href={banner.target?.startsWith('#') ? banner.target : '#planos'}>{banner.button} →</a>
        </div>}</div>
        <div className="hero-bottom">CONHEÇA A NOSSA ESTRUTURA <span>↓</span></div>
      </section>
      <section className="section" id="sobre"><div className="container about-layout">
        <div className="about-image">{about.image ? <img src={about.image} alt="Estrutura da academia" /> : <div className="about-image-placeholder"><span>IMPÉRIO</span><b>FITNESS</b></div>}</div>
        <div><span className="eyebrow">A ACADEMIA</span><h2>{about.title}</h2><p className="muted">{about.description}</p>
          <div className="features">{about.features.filter(f => f.active).map((f, i) => <div className="feature" key={i}><b>{String(i + 1).padStart(2, '0')}</b><div><strong>{f.title}</strong><p>{f.description}</p></div></div>)}</div>
          <div className="hours"><span className="eyebrow">HORÁRIOS DE FUNCIONAMENTO</span><p>{contact.hours}</p></div>
        </div>
      </div></section>
      <section className="section section-alt" id="planos"><div className="container">
        <span className="eyebrow">PLANOS E MATRÍCULAS</span><h2>ESCOLHA SEU PLANO</h2>
        <p className="muted">Encontre a opção ideal para sua rotina.</p>
        <div className="plans">{plans.filter(p => p.active).map(p => <article className={p.featured ? 'plan featured' : 'plan'} key={p.id}>
          {p.featured && <span className="tag">DESTAQUE</span>}<h3>{p.name}</h3><p className="muted">{p.description}</p>
          <div className="price">R$ {p.price}<small>{p.period}</small></div>
          <ul>{p.benefits.map((b, i) => <li key={i}>✓ {b}</li>)}</ul>
          <ExternalButton url={p.link}>Matricule-se →</ExternalButton>
        </article>)}</div>
        <p className="fine-print">Valores ilustrativos. Consulte a academia para confirmar preços e condições.</p>
      </div></section>
      <section className="section" id="galeria"><div className="container">
        <span className="eyebrow">GALERIA</span><h2>CONHEÇA O ESPAÇO</h2><p className="muted">Um pouco da estrutura da Império Fitness.</p>
        <div className="gallery">{gallery.filter(g => g.visible).map(g => <div className="gallery-item" key={g.id}>{g.image ? <img src={g.image} alt={g.caption} /> : <span className="gallery-symbol">✦</span>}<strong>{g.caption}</strong></div>)}</div>
      </div></section>
      <section className="section section-alt" id="contato"><div className="container">
        <span className="eyebrow">CONTATO</span><h2>FALE COM A GENTE</h2>
        <div className="contacts">
          <article className="contact-card"><span className="eyebrow">WHATSAPP</span><h3>{contact.whatsapp}</h3><ExternalButton url={wa}>Conversar no WhatsApp →</ExternalButton></article>
          <article className="contact-card"><span className="eyebrow">INSTAGRAM</span><h3>{contact.instagram}</h3><ExternalButton url={ig} className="outline-button">Abrir Instagram ↗</ExternalButton></article>
          <article className="contact-card"><span className="eyebrow">ENDEREÇO</span><h3>{contact.address}</h3><ExternalButton url={contact.address !== 'Rua Exemplo, 123 · Centro' ? 'https://www.google.com/maps/search/?api=1&query=' + encodeURIComponent(contact.address) : ''} className="outline-button">Abrir Google Maps ↗</ExternalButton></article>
        </div>
      </div></section>
    </main>
    <footer><div className="container footer-inner"><div className="brand">IMPÉRIO <strong>FITNESS</strong></div><span>© {new Date().getFullYear()} Império Fitness Academia</span></div></footer>
  </>
}
function AdminHome({ onBack, onPublished }) {
  const [draft, setDraft] = useState(loadDraft)
  const [message, setMessage] = useState('')
  const [preview, setPreview] = useState(false)
  const change = (section, field, value) => setDraft(d => ({ ...d, [section]: { ...d[section], [field]: value } }))
  const planChange = (id, field, value) => setDraft(d => ({ ...d, plans: d.plans.map(p => p.id === id ? { ...p, [field]: value } : p) }))
  const galleryChange = (id, field, value) => setDraft(d => ({ ...d, gallery: d.gallery.map(g => g.id === id ? { ...g, [field]: value } : g) }))
  const field = (label, value, onChange, multiline = false) => <label className="admin-field"><span>{label}</span>{multiline ? <textarea value={value || ''} onChange={e => onChange(e.target.value)} /> : <input value={value || ''} onChange={e => onChange(e.target.value)} />}</label>
  if (preview) return <><div className="preview-bar"><strong>Prévia não publicada</strong><button onClick={() => setPreview(false)}>Voltar à edição</button></div><Home data={draft} /></>
  return <div className="admin-shell"><div className="admin-top"><div><span className="eyebrow">PAINEL ADMINISTRATIVO · DEMONSTRAÇÃO</span><h1>ADMIN HOME</h1><p>Edite o conteúdo da Home V3. As alterações só aparecem na página pública depois de publicar.</p></div><button className="outline-button" onClick={onBack}>← Voltar à Home</button></div>
    <div className="admin-actions"><button className="outline-button" onClick={() => { saveDraft(draft); setMessage('Rascunho salvo neste navegador.') }}>Salvar rascunho</button><button className="outline-button" onClick={() => setPreview(true)}>Visualizar prévia</button><button className="gold-button" onClick={() => { saveDraft(draft); publishHome(draft); onPublished(); setMessage('Conteúdo publicado neste navegador.') }}>Publicar alterações</button></div>
    {message && <p className="admin-message">{message}</p>}
    <div className="admin-grid">
      <section className="admin-panel"><h2>Banner principal</h2>{field('Título', draft.banner.title, v => change('banner', 'title', v))}{field('Descrição', draft.banner.description, v => change('banner', 'description', v), true)}{field('Texto do botão', draft.banner.button, v => change('banner', 'button', v))}{field('Imagem (URL)', draft.banner.image, v => change('banner', 'image', v))}<label className="check"><input type="checkbox" checked={draft.banner.active} onChange={e => change('banner', 'active', e.target.checked)} /> Banner ativo</label></section>
      <section className="admin-panel"><h2>Apresentação</h2>{field('Título', draft.about.title, v => change('about', 'title', v))}{field('Descrição', draft.about.description, v => change('about', 'description', v), true)}{field('Imagem (URL)', draft.about.image, v => change('about', 'image', v))}</section>
      <section className="admin-panel"><h2>Contato</h2>{['whatsapp', 'waLink', 'instagram', 'igLink', 'address', 'hours'].map(k => <div key={k}>{field(k, draft.contact[k], v => change('contact', k, v), k === 'hours')}</div>)}</section>
      <section className="admin-panel"><h2>Planos</h2>{draft.plans.map(p => <div className="admin-subpanel" key={p.id}><h3>{p.name}</h3>{field('Nome', p.name, v => planChange(p.id, 'name', v))}{field('Descrição', p.description, v => planChange(p.id, 'description', v))}{field('Preço', p.price, v => planChange(p.id, 'price', v))}{field('Link de matrícula', p.link, v => planChange(p.id, 'link', v))}{field('Benefícios (um por linha)', p.benefits.join('\n'), v => planChange(p.id, 'benefits', v.split('\n').filter(Boolean)), true)}<label className="check"><input type="checkbox" checked={p.active} onChange={e => planChange(p.id, 'active', e.target.checked)} /> Ativo</label><label className="check"><input type="checkbox" checked={p.featured} onChange={e => planChange(p.id, 'featured', e.target.checked)} /> Destaque</label></div>)}</section>
      <section className="admin-panel"><h2>Galeria</h2>{draft.gallery.map(g => <div className="admin-subpanel" key={g.id}>{field('Legenda', g.caption, v => galleryChange(g.id, 'caption', v))}{field('Imagem (URL)', g.image, v => galleryChange(g.id, 'image', v))}<label className="check"><input type="checkbox" checked={g.visible} onChange={e => galleryChange(g.id, 'visible', e.target.checked)} /> Visível</label></div>)}</section>
    </div>
    <p className="admin-warning">Demonstração local: este painel ainda não possui autenticação. Não use para dados reais. A publicação compartilhada e o controle de acesso serão implementados na API Spring Boot.</p>
  </div>
}
export default function App() {
  const [published, setPublished] = useState(loadPublished)
  const [admin, setAdmin] = useState(window.location.pathname.startsWith('/admin'))
  useEffect(() => {
    const refresh = () => setPublished(loadPublished())
    window.addEventListener('storage', refresh)
    window.addEventListener('imperio-home-updated', refresh)
    const pop = () => setAdmin(window.location.pathname.startsWith('/admin'))
    window.addEventListener('popstate', pop)
    return () => { window.removeEventListener('storage', refresh); window.removeEventListener('imperio-home-updated', refresh); window.removeEventListener('popstate', pop) }
  }, [])
  const back = () => { window.history.pushState({}, '', '/'); setAdmin(false); setPublished(loadPublished()) }
  return admin ? <AdminHome onBack={back} onPublished={() => setPublished(loadPublished())} /> : <Home data={published || defaultHome} />
}
