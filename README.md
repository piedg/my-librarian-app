# API Documentation

## Libri

| Metodo | Endpoint | Accesso | Descrizione | Risposte |
| --- | --- | --- | --- | --- |
| GET | /books | Autenticato | Elenco libri. Filtri opzionali: `?title=` (ricerca parziale, case-insensitive) e `?available=true` (solo con copie disponibili) | 200 |
| GET | /books/{id} | Autenticato | Dettaglio di un libro | 200, 404 |
| GET | /books/category/{categoryId} | Autenticato | Libri di una categoria | 200, 404 |
| GET | /books/author/{authorId} | Autenticato | Libri di un autore | 200, 404 |
| POST | /books | ADMIN | Crea un libro; `availableCopies` parte uguale a `totalCopies` | 201, 400, 404, 409 |
| PUT | /books/{id} | ADMIN | Modifica un libro; le copie disponibili vengono ricalcolate in base ai prestiti in corso | 200, 400, 404, 409 |
| DELETE | /books/{id} | ADMIN | Elimina un libro, solo se non ha prestiti associati | 204, 404, 409 |

## Autori

| Metodo | Endpoint | Accesso | Descrizione | Risposte |
| --- | --- | --- | --- | --- |
| GET | /authors | Autenticato | Elenco autori. Filtri opzionali: `?name=` oppure `?surname=` (non combinabili: se passi entrambi vale solo name) | 200 |
| GET | /authors/{id} | Autenticato | Informazioni di un autore | 200, 404 |
| POST | /authors | ADMIN | Crea un autore | 201, 400 |
| PUT | /authors/{id} | ADMIN | Modifica un autore | 200, 400, 404 |
| DELETE | /authors/{id} | ADMIN | Elimina un autore | 204, 404 |

## Prestiti e prenotazioni

| Metodo | Endpoint | Accesso | Descrizione | Risposte |
| --- | --- | --- | --- | --- |
| POST | /loans | Autenticato | Prenota uno o più libri (`{"bookIds": [1, 2]}`) per l’utente del token. Operazione atomica: se un libro fallisce, nessuno viene prenotato | 201, 400, 404, 409 |
| GET | /loans/me | Autenticato | Prestiti dell’utente autenticato, filtro opzionale `?state=` | 200, 400 |
| GET | /loans/{id} | Proprietario o ADMIN | Dettaglio di un prestito | 200, 403, 404 |
| GET | /loans | ADMIN | Tutti i prestiti, filtri opzionali `?userId=` e `?state=` | 200, 400, 403, 404 |
| PATCH | /loans/{id}/pickup | ADMIN | Ritiro: `RESERVED` → `BORROWED`, scadenza spostata a +30 giorni | 200, 403, 404, 409 |
| PATCH | /loans/{id}/return | ADMIN | Riconsegna: `BORROWED` → `RETURNED`, la copia torna disponibile | 200, 403, 404, 409 |
| PATCH | /loans/{id}/cancel | Proprietario o ADMIN | Annulla una prenotazione: `RESERVED` → `CANCELLED`, la copia torna disponibile | 200, 403, 404, 409 |

## Ciclo di vita di un prestito

| Stato | Significato | Occupa una copia | Transizioni consentite |
| --- | --- | --- | --- |
| RESERVED | Prenotato, in attesa di ritiro (3 giorni) | Sì | BORROWED, CANCELLED, EXPIRED |
| BORROWED | Ritirato, in prestito (30 giorni) | Sì | RETURNED |
| RETURNED | Riconsegnato | No | Nessuna |
| CANCELLED | Prenotazione annullata | No | Nessuna |
| EXPIRED | Prenotazione scaduta senza ritiro | No | Nessuna |

## Codici di errore

Le risposte di errore usano il formato `ProblemDetail` (RFC 9457).

| Status | Quando |
| --- | --- |
| 400 | Dati non validi (con mappa `errors` per campo), JSON malformato, valore enum o id non valido |
| 401 | Token assente, scaduto o manomesso; credenziali di login errate |
| 403 | Utente autenticato ma senza permessi (ruolo o proprietà del prestito) |
| 404 | Libro, categoria, autore, utente o prestito inesistente |
| 409 | ISBN o email già esistenti, copie esaurite, prestito già attivo, limite superato, transizione di stato non valida, copie totali sotto quelle in prestito, libro con prestiti associati |
| 500 | Errore imprevisto (dettagli solo nel log) |

## Funcionalities

| Area | Stato |
| --- | --- |
| Catalogo libri (CRUD, ricerca, filtro disponibilità) | Completato e testato |
| Gestione errori centralizzata | Completato e testato |
| Prestiti e prenotazioni | Completato e testato |
| Scadenza automatica prenotazioni | Completato e testato |
| Autenticazione JWT e ruoli | Codice fornito, test da eseguire |
| Paginazione, test automatici, documentazione OpenAPI | Da fare |
