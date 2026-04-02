/* ===== MaVille — Single-Page App ===== */
const API = '';  // same origin

// ── State ────────────────────────────────────────────────
let token = null;
let currentUser = null; // { userId, nom, role }
let currentView = 'projets';

// ── Helpers ──────────────────────────────────────────────
async function api(method, path, body) {
    const opts = { method, headers: {} };
    if (token) opts.headers['Authorization'] = `Bearer ${token}`;
    if (body) {
        opts.headers['Content-Type'] = 'application/json';
        opts.body = JSON.stringify(body);
    }
    const res = await fetch(API + path, opts);
    if (res.status === 204) return null;
    const data = res.headers.get('content-type')?.includes('json') ? await res.json() : null;
    if (!res.ok) throw new Error(data?.message || data?.error || `Erreur ${res.status}`);
    return data;
}

function $(sel) { return document.querySelector(sel); }
function $$(sel) { return document.querySelectorAll(sel); }

function show(el) { el.classList.remove('hidden'); }
function hide(el) { el.classList.add('hidden'); }

function statusTag(statut) {
    const cls = statut.toLowerCase().replace('_', '-');
    const labels = { PREVU: 'Prévu', EN_COURS: 'En cours', SUSPENDU: 'Suspendu', TERMINE: 'Terminé' };
    return `<span class="tag ${cls}">${labels[statut] || statut}</span>`;
}

function typeLabel(t) {
    const map = {
        TRAVAUX_ROUTIERS: 'Routier', TRAVAUX_GAZ_ELECTRICITE: 'Gaz/Électricité',
        CONSTRUCTION_RENOVATION: 'Construction', ENTRETIEN_PAYSAGER: 'Paysager',
        TRANSPORTS_COMMUN: 'Transport', SIGNALISATION_ECLAIRAGE: 'Signalisation',
        TRAVAUX_SOUTERRAINS: 'Souterrain', TRAVAUX_RESIDENTIELS: 'Résidentiel',
        ENTRETIEN_URBAIN: 'Urbain', RESEAUX_TELECOMMUNICATION: 'Télécom'
    };
    return map[t] || t;
}

function escapeHtml(s) {
    if (!s) return '';
    return s.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}

// ── Auth ─────────────────────────────────────────────────
// Open auth modal
function openAuth(tab) {
    show($('#auth-overlay'));
    $$('.tab').forEach(t => t.classList.remove('active'));
    if (tab === 'register') {
        document.querySelector('.tab[data-tab="register"]').classList.add('active');
        hide($('#login-form')); show($('#register-form'));
    } else {
        document.querySelector('.tab[data-tab="login"]').classList.add('active');
        show($('#login-form')); hide($('#register-form'));
    }
    hide($('#auth-error'));
}

function closeAuth() { hide($('#auth-overlay')); }

$('#open-login-btn').addEventListener('click', () => openAuth('login'));
$('#open-register-btn').addEventListener('click', () => openAuth('register'));
$('#auth-close').addEventListener('click', closeAuth);
$('#auth-overlay').addEventListener('click', (e) => {
    if (e.target === $('#auth-overlay')) closeAuth();
});

// Hero login button(s)
document.addEventListener('click', (e) => {
    if (e.target.classList.contains('hero-login-btn')) openAuth('login');
});

document.querySelectorAll('.tab').forEach(tab => {
    tab.addEventListener('click', () => {
        $$('.tab').forEach(t => t.classList.remove('active'));
        tab.classList.add('active');
        if (tab.dataset.tab === 'login') {
            show($('#login-form')); hide($('#register-form'));
        } else {
            hide($('#login-form')); show($('#register-form'));
        }
        hide($('#auth-error'));
    });
});

$$('input[name="reg-role"]').forEach(r => {
    r.addEventListener('change', () => {
        if (r.value === 'resident') {
            show($('#resident-fields')); hide($('#intervenant-fields'));
        } else {
            hide($('#resident-fields')); show($('#intervenant-fields'));
        }
    });
});

$('#login-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
        hide($('#auth-error'));
        const data = await api('POST', '/api/auth/login', {
            courriel: $('#login-email').value,
            password: $('#login-password').value
        });
        loginSuccess(data);
    } catch (err) {
        showAuthError(err.message);
    }
});

$('#register-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const role = document.querySelector('input[name="reg-role"]:checked').value;
    try {
        hide($('#auth-error'));
        let data;
        if (role === 'resident') {
            data = await api('POST', '/api/auth/register/resident', {
                nom: $('#reg-nom').value,
                courriel: $('#reg-email').value,
                password: $('#reg-password').value,
                dateNaissance: $('#reg-dob').value || null,
                adresseResidentielle: $('#reg-adresse').value || null
            });
        } else {
            data = await api('POST', '/api/auth/register/intervenant', {
                nom: $('#reg-nom').value,
                courriel: $('#reg-email').value,
                password: $('#reg-password').value,
                type: $('#reg-type').value,
                identifiantVille: parseInt($('#reg-ville-id').value)
            });
        }
        loginSuccess(data);
    } catch (err) {
        showAuthError(err.message);
    }
});

function loginSuccess(data) {
    token = data.token;
    currentUser = { userId: data.userId, nom: data.nom, role: data.role };
    closeAuth();
    $('#user-name').textContent = data.nom + ' (' + data.role + ')';
    hide($('#guest-actions'));
    show($('#user-actions'));
    applyRoleVisibility();
    navigateTo('projets');
    pollNotifications();
}

function showAuthError(msg) {
    const el = $('#auth-error');
    el.textContent = msg;
    show(el);
}

$('#logout-btn').addEventListener('click', () => {
    token = null;
    currentUser = null;
    show($('#guest-actions'));
    hide($('#user-actions'));
    applyRoleVisibility();
    navigateTo('home');
    $('#login-email').value = '';
    $('#login-password').value = '';
});

function applyRoleVisibility() {
    if (!currentUser) {
        $$('.auth-only').forEach(el => el.classList.add('nav-hidden'));
        $$('.resident-only').forEach(el => hide(el));
        $$('.intervenant-only').forEach(el => hide(el));
        return;
    }
    $$('.auth-only').forEach(el => el.classList.remove('nav-hidden'));
    const isResident = currentUser.role === 'RESIDENT';
    $$('.resident-only').forEach(el => isResident ? show(el) : hide(el));
    $$('.intervenant-only').forEach(el => !isResident ? show(el) : hide(el));
}

// ── Navigation ───────────────────────────────────────────
$$('.nav-item').forEach(btn => {
    btn.addEventListener('click', () => navigateTo(btn.dataset.view));
});

function navigateTo(view) {
    currentView = view;
    $$('.nav-item').forEach(n => n.classList.remove('active'));
    document.querySelector(`.nav-item[data-view="${view}"]`)?.classList.add('active');
    $$('.view').forEach(v => hide(v));
    show($(`#view-${view}`));
    loadView(view);
}

async function loadView(view) {
    try {
        switch (view) {
            case 'home': break;
            case 'projets': await loadProjets(); break;
            case 'requetes': await loadRequetes(); break;
            case 'horaires': await loadHoraires(); break;
            case 'candidatures': await loadCandidatures(); break;
            case 'travaux': await loadTravaux(); break;
            case 'entraves': await loadEntraves(); break;
            case 'notifications': await loadNotifications(); break;
        }
    } catch (err) {
        console.error('Error loading view:', err);
    }
}

// ── PROJETS ──────────────────────────────────────────────
async function loadProjets() {
    const quartier = $('#projets-search').value.trim();
    const statut = $('#projets-statut-filter').value;
    let url = '/api/projets';
    const params = [];
    if (quartier) params.push(`quartier=${encodeURIComponent(quartier)}`);
    if (statut) params.push(`statut=${statut}`);
    if (params.length) url += '?' + params.join('&');

    const projets = await api('GET', url);
    const container = $('#projets-list');

    if (!projets.length) {
        container.innerHTML = '<div class="empty-state"><div class="emoji">📋</div><p>Aucun projet trouvé</p></div>';
        return;
    }

    container.innerHTML = projets.map(p => `
        <div class="card">
            <div class="card-title">${escapeHtml(p.titre)}</div>
            <div class="card-meta">${typeLabel(p.typeTravaux)} · ${escapeHtml(p.quartierAffecte)} · ${escapeHtml(p.dateDebut)} → ${escapeHtml(p.dateFin || '?')}</div>
            <div class="card-desc">${escapeHtml(p.description)}</div>
            <div class="card-footer">
                ${statusTag(p.statutProjet)}
                <span class="card-meta">Par ${escapeHtml(p.intervenant?.nom || '—')}</span>
                ${currentUser?.role === 'INTERVENANT' && p.intervenant?.id === currentUser.userId ? `
                    <div class="card-actions">
                        <button class="btn small secondary" onclick="openUpdateStatut(${p.id}, '${p.statutProjet}')">Changer statut</button>
                    </div>` : ''}
            </div>
        </div>
    `).join('');
}

$('#projets-search').addEventListener('keydown', e => { if (e.key === 'Enter') loadProjets(); });
$('#projets-statut-filter').addEventListener('change', loadProjets);

$('#add-projet-btn').addEventListener('click', () => {
    openModal('Nouveau projet', `
        <form id="create-projet-form">
            <div class="field"><label>Titre</label><input id="cp-titre" required></div>
            <div class="field"><label>Description</label><textarea id="cp-desc" rows="3"></textarea></div>
            <div class="field"><label>Type de travaux</label>
                <select id="cp-type">
                    <option value="TRAVAUX_ROUTIERS">Routier</option>
                    <option value="TRAVAUX_GAZ_ELECTRICITE">Gaz/Électricité</option>
                    <option value="CONSTRUCTION_RENOVATION">Construction/Rénovation</option>
                    <option value="ENTRETIEN_PAYSAGER">Paysager</option>
                    <option value="TRANSPORTS_COMMUN">Transport en commun</option>
                    <option value="SIGNALISATION_ECLAIRAGE">Signalisation/Éclairage</option>
                    <option value="TRAVAUX_SOUTERRAINS">Souterrain</option>
                    <option value="TRAVAUX_RESIDENTIELS">Résidentiel</option>
                    <option value="ENTRETIEN_URBAIN">Entretien urbain</option>
                    <option value="RESEAUX_TELECOMMUNICATION">Télécommunication</option>
                </select>
            </div>
            <div class="field"><label>Date début</label><input type="date" id="cp-debut" required></div>
            <div class="field"><label>Date fin</label><input type="date" id="cp-fin"></div>
            <div class="field"><label>Quartier affecté</label><input id="cp-quartier" required></div>
            <button type="submit" class="btn primary">Créer le projet</button>
        </form>
    `);
    $('#create-projet-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        await api('POST', '/api/projets', {
            titre: $('#cp-titre').value,
            description: $('#cp-desc').value,
            typeTravaux: $('#cp-type').value,
            dateDebut: $('#cp-debut').value,
            dateFin: $('#cp-fin').value || null,
            quartierAffecte: $('#cp-quartier').value
        });
        closeModal();
        loadProjets();
    });
});

window.openUpdateStatut = function(id, current) {
    openModal('Changer le statut', `
        <form id="update-statut-form">
            <div class="field"><label>Nouveau statut</label>
                <select id="us-statut">
                    <option value="PREVU" ${current==='PREVU'?'selected':''}>Prévu</option>
                    <option value="EN_COURS" ${current==='EN_COURS'?'selected':''}>En cours</option>
                    <option value="SUSPENDU" ${current==='SUSPENDU'?'selected':''}>Suspendu</option>
                    <option value="TERMINE" ${current==='TERMINE'?'selected':''}>Terminé</option>
                </select>
            </div>
            <button type="submit" class="btn primary">Mettre à jour</button>
        </form>
    `);
    $('#update-statut-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        await api('PATCH', `/api/projets/${id}/statut`, { statutProjet: $('#us-statut').value });
        closeModal();
        loadProjets();
    });
};

// ── REQUETES ─────────────────────────────────────────────
async function loadRequetes() {
    const openOnly = $('#requetes-open-filter').checked;
    let url = '/api/requetes';
    if (openOnly) url += '?ouvert=true';

    const requetes = await api('GET', url);
    const container = $('#requetes-list');

    if (!requetes.length) {
        container.innerHTML = '<div class="empty-state"><div class="emoji">📝</div><p>Aucune requête trouvée</p></div>';
        return;
    }

    container.innerHTML = requetes.map(r => `
        <div class="card">
            <div class="card-title">${escapeHtml(r.titreDuTravail)}</div>
            <div class="card-meta">${typeLabel(r.typeDeTravaux)} · Début: ${escapeHtml(r.dateDeDebut || '—')}</div>
            <div class="card-desc">${escapeHtml(r.description)}</div>
            <div class="card-footer">
                <span class="tag ${r.ouvert ? 'ouvert' : 'ferme'}">${r.ouvert ? 'Ouverte' : 'Fermée'}</span>
                <span class="card-meta">Par ${escapeHtml(r.proprietaire?.nom || '—')}</span>
                <div class="card-actions">
                    ${currentUser?.role === 'INTERVENANT' && r.ouvert ? `<button class="btn small success" onclick="submitCandidature(${r.id})">Postuler</button>` : ''}
                    ${currentUser?.role === 'RESIDENT' && r.proprietaire?.id === currentUser.userId && r.ouvert ? `<button class="btn small danger" onclick="closeRequete(${r.id})">Fermer</button>` : ''}
                </div>
            </div>
        </div>
    `).join('');
}

$('#requetes-open-filter').addEventListener('change', loadRequetes);

$('#add-requete-btn').addEventListener('click', () => {
    openModal('Nouvelle requête', `
        <form id="create-requete-form">
            <div class="field"><label>Titre du travail</label><input id="cr-titre" required></div>
            <div class="field"><label>Description</label><textarea id="cr-desc" rows="3"></textarea></div>
            <div class="field"><label>Date de début souhaitée</label><input type="date" id="cr-debut" required></div>
            <div class="field"><label>Type de travaux</label>
                <select id="cr-type">
                    <option value="TRAVAUX_ROUTIERS">Routier</option>
                    <option value="TRAVAUX_GAZ_ELECTRICITE">Gaz/Électricité</option>
                    <option value="CONSTRUCTION_RENOVATION">Construction/Rénovation</option>
                    <option value="ENTRETIEN_PAYSAGER">Paysager</option>
                    <option value="TRANSPORTS_COMMUN">Transport en commun</option>
                    <option value="SIGNALISATION_ECLAIRAGE">Signalisation/Éclairage</option>
                    <option value="TRAVAUX_SOUTERRAINS">Souterrain</option>
                    <option value="TRAVAUX_RESIDENTIELS">Résidentiel</option>
                    <option value="ENTRETIEN_URBAIN">Entretien urbain</option>
                    <option value="RESEAUX_TELECOMMUNICATION">Télécommunication</option>
                </select>
            </div>
            <button type="submit" class="btn primary">Soumettre</button>
        </form>
    `);
    $('#create-requete-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        await api('POST', '/api/requetes', {
            titreDuTravail: $('#cr-titre').value,
            description: $('#cr-desc').value,
            dateDeDebut: $('#cr-debut').value,
            typeDeTravaux: $('#cr-type').value
        });
        closeModal();
        loadRequetes();
    });
});

window.closeRequete = async function(id) {
    if (!confirm('Fermer cette requête ?')) return;
    await api('PATCH', `/api/requetes/${id}/close`);
    loadRequetes();
};

window.submitCandidature = async function(requeteId) {
    try {
        await api('POST', `/api/candidatures/requete/${requeteId}`);
        alert('Candidature soumise !');
        loadRequetes();
    } catch (err) {
        alert(err.message);
    }
};

// ── HORAIRES ─────────────────────────────────────────────
async function loadHoraires() {
    const horaires = await api('GET', '/api/horaires');
    const container = $('#horaires-list');

    const dayLabels = {
        LUNDI: 'Lundi', MARDI: 'Mardi', MERCREDI: 'Mercredi',
        JEUDI: 'Jeudi', VENDREDI: 'Vendredi', SAMEDI: 'Samedi', DIMANCHE: 'Dimanche'
    };

    container.innerHTML = horaires.map(h => `
        <div class="card horaire-card" id="horaire-${h.id}">
            <span class="horaire-day">${dayLabels[h.jourDeLaSemaine] || h.jourDeLaSemaine}</span>
            <div class="horaire-inputs">
                <input type="time" id="hd-${h.id}" value="${h.heureDebut === 'Pas spécifié' ? '' : h.heureDebut}" placeholder="Début">
                <span>→</span>
                <input type="time" id="hf-${h.id}" value="${h.heureFin === 'Pas spécifié' ? '' : h.heureFin}" placeholder="Fin">
                <button class="btn small primary" onclick="updateHoraire(${h.id})">💾</button>
            </div>
        </div>
    `).join('');
}

window.updateHoraire = async function(id) {
    const debut = document.getElementById(`hd-${id}`).value || 'Pas spécifié';
    const fin = document.getElementById(`hf-${id}`).value || 'Pas spécifié';
    await api('PATCH', `/api/horaires/${id}`, { heureDebut: debut, heureFin: fin });
    loadHoraires();
};

// ── CANDIDATURES ─────────────────────────────────────────
async function loadCandidatures() {
    const candidatures = await api('GET', '/api/candidatures/mine');
    const container = $('#candidatures-list');

    if (!candidatures.length) {
        container.innerHTML = '<div class="empty-state"><div class="emoji">📨</div><p>Aucune candidature</p></div>';
        return;
    }

    container.innerHTML = candidatures.map(c => `
        <div class="card">
            <div class="card-title">${escapeHtml(c.requete?.titreDuTravail || '—')}</div>
            <div class="card-meta">${typeLabel(c.requete?.typeDeTravaux)} · ${c.requete?.ouvert ? 'Ouverte' : 'Fermée'}</div>
            <div class="card-desc">${escapeHtml(c.requete?.description || '')}</div>
            <div class="card-footer">
                <span class="tag ${c.requete?.ouvert ? 'ouvert' : 'ferme'}">${c.requete?.ouvert ? 'Ouverte' : 'Fermée'}</span>
                <button class="btn small danger" onclick="withdrawCandidature(${c.id})">Retirer</button>
            </div>
        </div>
    `).join('');
}

window.withdrawCandidature = async function(id) {
    if (!confirm('Retirer cette candidature ?')) return;
    await api('DELETE', `/api/candidatures/${id}`);
    loadCandidatures();
};

// ── TRAVAUX MTL ──────────────────────────────────────────
async function loadTravaux() {
    const q = $('#travaux-search').value.trim();
    let url = '/api/travaux?limit=30';
    if (q) url += `&quartier=${encodeURIComponent(q)}`;
    const data = await api('GET', url);
    const container = $('#travaux-list');
    const records = data?.records || [];

    if (!records.length) {
        container.innerHTML = '<div class="empty-state"><div class="emoji">🚧</div><p>Aucun travail trouvé</p></div>';
        return;
    }

    container.innerHTML = records.map(r => `
        <div class="card">
            <div class="card-title">${escapeHtml(r.reason_category || r.permitcategory || 'Travaux')}</div>
            <div class="card-meta">${escapeHtml(r.boroughid || '')} · ${escapeHtml(r.duration_start_date?.substring(0,10) || '')} → ${escapeHtml(r.duration_end_date?.substring(0,10) || '?')}</div>
            <div class="card-desc">${escapeHtml(r.occupancy_name || r.currentstatus || '')}</div>
            <div class="card-meta">${escapeHtml(r.organizationname || '')}</div>
        </div>
    `).join('');
}

$('#travaux-search-btn').addEventListener('click', loadTravaux);
$('#travaux-search').addEventListener('keydown', e => { if (e.key === 'Enter') loadTravaux(); });

// ── ENTRAVES MTL ─────────────────────────────────────────
async function loadEntraves() {
    const q = $('#entraves-search').value.trim();
    let url = '/api/entraves?limit=30';
    if (q) url += `&rue=${encodeURIComponent(q)}`;
    const data = await api('GET', url);
    const container = $('#entraves-list');
    const records = data?.records || [];

    if (!records.length) {
        container.innerHTML = '<div class="empty-state"><div class="emoji">🚫</div><p>Aucune entrave trouvée</p></div>';
        return;
    }

    container.innerHTML = records.map(r => `
        <div class="card">
            <div class="card-title">${escapeHtml(r.id_request || 'Entrave')}</div>
            <div class="card-meta">${escapeHtml(r.streetid || '')} — ${escapeHtml(r.streetimpacttype || '')}</div>
            <div class="card-desc">Impact: ${escapeHtml(r.streetimpactwidth || '—')}</div>
        </div>
    `).join('');
}

$('#entraves-search-btn').addEventListener('click', loadEntraves);
$('#entraves-search').addEventListener('keydown', e => { if (e.key === 'Enter') loadEntraves(); });

// ── NOTIFICATIONS ────────────────────────────────────────
async function loadNotifications() {
    const notifs = await api('GET', '/api/notifications');
    const container = $('#notifications-list');

    if (!notifs.length) {
        container.innerHTML = '<div class="empty-state"><div class="emoji">🔔</div><p>Aucune notification</p></div>';
        return;
    }

    container.innerHTML = notifs.map(n => `
        <div class="card notif-card ${n.lu ? 'read' : ''}">
            <div style="display:flex;align-items:center;justify-content:space-between;gap:12px">
                <div class="card-desc" style="margin:0;flex:1">${escapeHtml(n.message)}</div>
                <div class="card-actions">
                    <span class="tag ${n.lu ? 'read' : 'unread'}">${n.lu ? 'Lu' : 'Non lu'}</span>
                    ${!n.lu ? `<button class="btn small secondary" onclick="markRead(${n.id})">Marquer lu</button>` : ''}
                </div>
            </div>
        </div>
    `).join('');
}

window.markRead = async function(id) {
    await api('PATCH', `/api/notifications/${id}/read`);
    loadNotifications();
    pollNotifications();
};

// ── Notification badge polling ───────────────────────────
async function pollNotifications() {
    try {
        const unread = await api('GET', '/api/notifications/unread');
        const badge = $('#notif-badge');
        if (unread.length > 0) {
            badge.textContent = unread.length;
            show(badge);
        } else {
            hide(badge);
        }
    } catch { /* ignore */ }
}

$('#notif-btn').addEventListener('click', () => navigateTo('notifications'));

setInterval(() => { if (token) pollNotifications(); }, 30000);

// ── Modal ────────────────────────────────────────────────
function openModal(title, bodyHtml) {
    $('#modal-title').textContent = title;
    $('#modal-body').innerHTML = bodyHtml;
    show($('#modal-overlay'));
}

function closeModal() {
    hide($('#modal-overlay'));
}

$('#modal-close').addEventListener('click', closeModal);
$('#modal-overlay').addEventListener('click', (e) => {
    if (e.target === $('#modal-overlay')) closeModal();
});

// ── Home link ────────────────────────────────────────────
const homeLink = $('#home-link');
if (homeLink) homeLink.addEventListener('click', () => navigateTo('home'));

// ── Initial load ─────────────────────────────────────────
show($('#guest-actions'));
hide($('#user-actions'));
applyRoleVisibility();
navigateTo('home');
