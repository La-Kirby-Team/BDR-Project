SELECT
    p.nom AS produit,
    ms.quantite AS quantite,
    a.dateCommande AS date_commande,
    CURRENT_DATE - a.dateCommande AS jours_depuis_commande
FROM
    Approvisionnement a
        JOIN
    MouvementStock ms ON a.idMouvementStock = ms.id
        JOIN
    Produit p ON ms.idProduit = p.id
WHERE
    ms.date IS NULL
ORDER BY
    a.dateCommande ASC;
