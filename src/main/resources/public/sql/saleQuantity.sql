SELECT idProduit
FROM Article
WHERE idProduit = (SELECT idProduit
                   FROM Produit
                   WHERE nom = ?
                     AND recipient = ?
    ::typeRecipient
  AND volume = ?
  AND tauxAlcool = ?)
  AND prix = ?;