SELECT idProduit
FROM Article
WHERE idProduit = ?
  AND recipient = ?::typeRecipient
  AND volume = ?
  AND datePeremption = ?
  AND dateFinDeVente = ?
  AND prix != ?;