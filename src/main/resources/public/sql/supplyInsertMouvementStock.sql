INSERT INTO MouvementStock (idMagasin, idProduit, volume, recipient, quantite)
VALUES (?, ?, ?, ?::typeRecipient, ?) RETURNING id;