INSERT INTO Produit (idProvenance, nom, tauxAlcool)
VALUES (?, ?, ?) RETURNING idProduit;