SELECT
    p.nom AS "Nom du produit",
    COALESCE(a.volume, ms.volume) AS "Volume",
    COALESCE(a.recipient, ms.recipient) AS "Recipient",
    SUM(
            CASE
                WHEN EXISTS (SELECT 1 FROM Approvisionnement ap WHERE ap.idMouvementStock = ms.id)
                    THEN ms.quantite -- Approvisionnement (positive)
                WHEN EXISTS (SELECT 1 FROM Vente v WHERE v.idMouvementStock = ms.id)
                    THEN -ms.quantite -- Vente (negative)
                ELSE 0
                END
    ) AS "Quantité"
FROM Produit p
         LEFT JOIN Article a ON p.idProduit = a.idProduit
         LEFT JOIN MouvementStock ms
                   ON p.idProduit = ms.idProduit
                       AND (a.volume = ms.volume OR a.volume IS NULL)
                       AND (a.recipient = ms.recipient OR a.recipient IS NULL)
GROUP BY p.nom, a.volume, a.recipient, ms.volume, ms.recipient
ORDER BY p.nom;
