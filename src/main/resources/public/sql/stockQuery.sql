SELECT
    p.idProduit AS "ID",
    p.nom AS "Nom du produit",
    a.volume AS "Volume",
    a.recipient AS "Recipient",
    COALESCE(
            SUM(
                    CASE
                        WHEN v.idMouvementStock IS NOT NULL THEN -ms.quantite  -- Subtract for sales
                        WHEN ap.idMouvementStock IS NOT NULL THEN ms.quantite  -- Add for supplies
                        ELSE 0
                        END
            ), 0
    ) AS "Quantité"
FROM Produit p
         LEFT JOIN Article a ON p.idProduit = a.idProduit
         LEFT JOIN MouvementStock ms
                   ON p.idProduit = ms.idProduit
                       AND a.volume = ms.volume
                       AND a.recipient = ms.recipient
                       AND ms.date IS NOT NULL  -- ❗ Ignore movements with NULL dates
         LEFT JOIN Vente v
                   ON ms.id = v.idMouvementStock  -- If a stock movement exists in Vente, it's a sale
         LEFT JOIN Approvisionnement ap
                   ON ms.id = ap.idMouvementStock  -- If a stock movement exists in Approvisionnement, it's a supply
GROUP BY p.idProduit, p.nom, a.volume, a.recipient
ORDER BY p.nom;
