// === ÉTAT ===
let selectedMood = null;
let selectedSymptoms = [];
let selectedFlow = null;
let selectedMucus = null;
let currentDate = new Date().toISOString().split('T')[0];

// === INITIALISATION ===
document.addEventListener('DOMContentLoaded', () => {
    // Lire la date depuis les params URL si fournie
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('date')) {
        currentDate = urlParams.get('date');
    }

    document.getElementById('logDate').value = currentDate;
    updateDateLabel();
    loadLogForDate(currentDate);

    // Listeners
    document.getElementById('isPeriodDay').addEventListener('change', function() {
        document.getElementById('flowSection').style.display = this.checked ? 'block' : 'none';
    });

    document.getElementById('sexualActivity').addEventListener('change', function() {
        document.getElementById('contraceptionRow').style.display = this.checked ? 'flex' : 'none';
    });

    document.getElementById('datePrev').addEventListener('click', () => changeDate(-1));
    document.getElementById('dateNext').addEventListener('click', () => changeDate(1));
    document.getElementById('logDate').addEventListener('change', function() {
        currentDate = this.value;
        updateDateLabel();
        loadLogForDate(currentDate);
    });

    // Mood buttons
    document.querySelectorAll('.mood-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            document.querySelectorAll('.mood-btn').forEach(b => b.classList.remove('selected'));
            this.classList.add('selected');
            selectedMood = this.dataset.mood;
        });
    });

    // Symptom buttons
    document.querySelectorAll('.symptom-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            this.classList.toggle('selected');
            const s = this.dataset.symptom;
            if (selectedSymptoms.includes(s)) {
                selectedSymptoms = selectedSymptoms.filter(x => x !== s);
            } else {
                selectedSymptoms.push(s);
            }
        });
    });

    // Flow chips
    document.querySelectorAll('.flow-chip[data-flow]').forEach(btn => {
        btn.addEventListener('click', function() {
            document.querySelectorAll('.flow-chip[data-flow]').forEach(b => b.classList.remove('selected'));
            this.classList.add('selected');
            selectedFlow = this.dataset.flow;
        });
    });

    // Mucus chips
    document.querySelectorAll('.flow-chip[data-mucus]').forEach(btn => {
        btn.addEventListener('click', function() {
            document.querySelectorAll('.flow-chip[data-mucus]').forEach(b => b.classList.remove('selected'));
            this.classList.add('selected');
            selectedMucus = this.dataset.mucus;
        });
    });
});

// === DATE NAVIGATION ===
function changeDate(delta) {
    const d = new Date(currentDate + 'T12:00:00');
    d.setDate(d.getDate() + delta);
    // Ne pas dépasser aujourd'hui
    if (d > new Date()) return;
    currentDate = d.toISOString().split('T')[0];
    document.getElementById('logDate').value = currentDate;
    updateDateLabel();
    loadLogForDate(currentDate);
}

function updateDateLabel() {
    const d = new Date(currentDate + 'T12:00:00');
    const today = new Date().toISOString().split('T')[0];
    const yesterday = new Date(Date.now() - 86400000).toISOString().split('T')[0];
    let label;
    if (currentDate === today) label = "Aujourd'hui";
    else if (currentDate === yesterday) label = "Hier";
    else label = d.toLocaleDateString('fr-FR', {day:'numeric', month:'long'});
    document.getElementById('dateLabel').textContent = label;
    document.getElementById('dateNext').style.opacity = currentDate === today ? '0.3' : '1';
}

// === CHARGEMENT LOG EXISTANT ===
function loadLogForDate(date) {
    resetForm();
    fetch(`/api/logs/${date}`)
        .then(r => r.json())
        .then(log => {
            if (!log) {
                document.getElementById('deleteBtn').style.display = 'none';
                return;
            }
            populateForm(log);
            document.getElementById('deleteBtn').style.display = 'block';
        })
        .catch(() => {
            document.getElementById('deleteBtn').style.display = 'none';
        });
}

function resetForm() {
    selectedMood = null; selectedSymptoms = []; selectedFlow = null; selectedMucus = null;
    document.querySelectorAll('.mood-btn, .symptom-btn, .flow-chip').forEach(b => b.classList.remove('selected'));
    document.getElementById('isPeriodDay').checked = false;
    document.getElementById('flowSection').style.display = 'none';
    document.getElementById('sexualActivity').checked = false;
    document.getElementById('usedContraception').checked = false;
    document.getElementById('contraceptionRow').style.display = 'none';
    document.getElementById('basalTemp').value = '';
    document.getElementById('logNotes').value = '';
}

function populateForm(log) {
    if (log.mood) {
        selectedMood = log.mood;
        document.querySelector(`.mood-btn[data-mood="${log.mood}"]`)?.classList.add('selected');
    }
    if (log.symptoms) {
        selectedSymptoms = log.symptoms;
        log.symptoms.forEach(s => {
            document.querySelector(`.symptom-btn[data-symptom="${s}"]`)?.classList.add('selected');
        });
    }
    if (log.isPeriodDay) {
        document.getElementById('isPeriodDay').checked = true;
        document.getElementById('flowSection').style.display = 'block';
    }
    if (log.flowIntensity) {
        selectedFlow = log.flowIntensity;
        document.querySelector(`.flow-chip[data-flow="${log.flowIntensity}"]`)?.classList.add('selected');
    }
    if (log.cervicalMucus) {
        selectedMucus = log.cervicalMucus;
        document.querySelector(`.flow-chip[data-mucus="${log.cervicalMucus}"]`)?.classList.add('selected');
    }
    if (log.sexualActivity) {
        document.getElementById('sexualActivity').checked = true;
        document.getElementById('contraceptionRow').style.display = 'flex';
    }
    if (log.usedContraception) document.getElementById('usedContraception').checked = true;
    if (log.basalTemperature) document.getElementById('basalTemp').value = log.basalTemperature;
    if (log.notes) document.getElementById('logNotes').value = log.notes;
}

// === SAUVEGARDE ===
function saveLog() {
    const payload = {
        isPeriodDay: document.getElementById('isPeriodDay').checked,
        flowIntensity: selectedFlow,
        mood: selectedMood,
        symptoms: selectedSymptoms,
        cervicalMucus: selectedMucus,
        sexualActivity: document.getElementById('sexualActivity').checked,
        usedContraception: document.getElementById('usedContraception').checked,
        basalTemperature: document.getElementById('basalTemp').value ? parseFloat(document.getElementById('basalTemp').value) : null,
        notes: document.getElementById('logNotes').value || null
    };

    fetch(`/api/logs/${currentDate}`, {
        method: 'POST',
        headers: {'Content-Type':'application/json'},
        body: JSON.stringify(payload)
    })
    .then(r => {
        if (r.ok) {
            showToast('Journalisé avec succès ✓');
            document.getElementById('deleteBtn').style.display = 'block';
        } else {
            showToast('Erreur lors de la sauvegarde');
        }
    })
    .catch(() => showToast('Erreur réseau'));
}

// === SUPPRESSION ===
function deleteLog() {
    if (!confirm('Supprimer ce log ?')) return;
    fetch(`/api/logs/${currentDate}`, {method: 'DELETE'})
        .then(() => {
            resetForm();
            document.getElementById('deleteBtn').style.display = 'none';
            showToast('Log supprimé');
        });
}

// === TOAST ===
function showToast(msg) {
    const t = document.getElementById('toast');
    t.textContent = msg;
    t.classList.add('show');
    setTimeout(() => t.classList.remove('show'), 2500);
}
