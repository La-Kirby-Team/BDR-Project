SELECT
    p.nom AS produit,
    ms.quantite AS quantite,
    a.dateCommande AS dateCommande,
    CURRENT_DATE - a.dateCommande AS joursDepuisCommande,
    ms.id AS mouvementStockId
FROM
    Approvisionnement a
        INNER JOIN
    MouvementStock ms ON a.idMouvementStock = ms.id
        INNER JOIN
    Article art ON ms.idProduit = art.idProduit
        INNER JOIN
    Produit p ON art.idProduit = p.id
WHERE
    ms.date IS NULL
ORDER BY
    a.dateCommande;
