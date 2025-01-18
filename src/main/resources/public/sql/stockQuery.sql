SELECT
    ms.id AS "ID",
    p.nom AS "Nom du produit",
    COALESCE(a.volume, ms.volume) AS "Volume",
    COALESCE(a.recipient, ms.recipient) AS "Recipient",
    COALESCE(SUM(ms.quantite), 0) AS "Quantité"
FROM Produit p
         LEFT JOIN Article a ON p.idProduit = a.idProduit
         LEFT JOIN MouvementStock ms
                   ON p.idProduit = ms.idProduit
                       AND (a.volume = ms.volume OR a.volume IS NULL)
                       AND (a.recipient = ms.recipient OR a.recipient IS NULL)
GROUP BY ms.id, p.nom, a.volume, a.recipient, ms.volume, ms.recipient
ORDER BY p.nom;
