SELECT p.nom AS produit, COALESCE(SUM(CASE
        WHEN ms.id IN (SELECT idMouvementStock FROM Approvisionnement) THEN ms.quantite
        WHEN ms.id IN (SELECT idMouvementStock FROM Vente) THEN -ms.quantite
        ELSE 0
    END), 0) AS quantite_totale
FROM
Produit p
LEFT JOIN
Article a ON p.idProduit = a.idProduit
LEFT JOIN
MouvementStock ms ON a.idProduit = ms.idProduit
AND a.volume = ms.volume
AND a.recipient = ms.recipient
GROUP BY
p.nom
HAVING
COALESCE(SUM(CASE
            WHEN ms.id IN (SELECT idMouvementStock FROM Approvisionnement) THEN ms.quantite
            WHEN ms.id IN (SELECT idMouvementStock FROM Vente) THEN -ms.quantite
            ELSE 0
            END), 0) <= 3
ORDER BY
p.nom;