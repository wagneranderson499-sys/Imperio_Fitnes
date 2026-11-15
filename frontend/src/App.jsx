import { useEffect, useState } from 'react'
import { defaultHome, loadPublished, loadDraft, saveDraft, publishHome } from './homeData'
import './App.css'

function ExternalButton({ url, children, className = 'gold-button' }) {
  return <a className={className} href={/^https?:\/\//i.test(url || '') ? url : '#contato'} target={/^https?:\/\//i.test(url || '') ? '_blank' : undefined} rel="noreferrer">{children}</a>
}
// The original V3 document is served unchanged for pixel-faithful rendering.
// It shares the same origin and localStorage keys with the admin editor.
function Home({ preview = false, data }) {
  useEffect(() => {
    if (preview && data) sessionStorage.setItem('imperio_preview', JSON.stringify(data))
    return () => { if (preview) sessionStorage.removeItem('imperio_preview') }
  }, [preview, data])
  return <iframe title={preview ? 'Prévia da Home V3' : 'Império Fitness Home V3'} src={preview ? '/home-v3.html?preview=1' : '/home-v3.html'} className="original-home-frame" />
}
function AdminHome({ onBack, onPublished }) {
  const [draft, setDraft] = useState(loadDraft)
  const [message, setMessage] = useState('')
  const [preview, setPreview] = useState(false)
  const change = (section, field, value) => setDraft(d => ({ ...d, [section]: { ...d[section], [field]: value } }))
  const planChange = (id, field, value) => setDraft(d => ({ ...d, plans: d.plans.map(p => p.id === id ? { ...p, [field]: value } : p) }))
  const galleryChange = (id, field, value) => setDraft(d => ({ ...d, gallery: d.gallery.map(g => g.id === id ? { ...g, [field]: value } : g) }))
  const field = (label, value, onChange, multiline = false) => <label className="admin-field"><span>{label}</span>{multiline ? <textarea value={value || ''} onChange={e => onChange(e.target.value)} /> : <input value={value || ''} onChange={e => onChange(e.target.value)} />}</label>
  if (preview) return <><div className="preview-bar"><strong>Prévia não publicada</strong><button onClick={() => setPreview(false)}>Voltar à edição</button></div><Home preview data={draft} /></>
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
  return admin ? <iframe title="Painel Administrativo Império Fitness V4" src="/admin-v4.html" className="original-home-frame" /> : <Home data={published || defaultHome} />
}
