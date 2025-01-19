-- Remplissage de la table Provenance
INSERT INTO Provenance (pays, region, producteur) VALUES
                                                      ('France', 'Bordeaux', 'Château Margaux'),
                                                      ('Italie', 'Toscane', 'Antinori'),
                                                      ('Espagne', 'Ribera del Duero', 'Bodega Vega Sicilia'),
                                                      ('USA', 'Napa Valley', 'Robert Mondavi'),
                                                      ('France', 'Champagne', 'Moët & Chandon');

-- Remplissage de la table Produit
INSERT INTO Produit (nom, tauxAlcool, idProvenance) VALUES
                                                        ('Vin Rouge Bordeaux', 13.5, 1),
                                                        ('Vin Blanc Chardonnay', 12.0, 2),
                                                        ('Champagne Brut', 12.5, 2),
                                                        ('Whisky Single Malt', 40.0, 3),
                                                        ('Bière Blonde', 5.0, 1);

-- Remplissage de la table Article
INSERT INTO Article (idProduit, volume, recipient, prix, datePeremption, dateFinDeVente) VALUES
                                                                                             (1, 750, 'bouteille', 45.99, '2025-12-31', NULL),
                                                                                             (1, 1500, 'bouteille', 89.99, '2025-12-31', NULL),
                                                                                             (2, 750, 'bouteille', 35.99, '2024-11-30', '2024-10-01'),
                                                                                             (3, 750, 'bouteille', 55.00, '2026-06-30', NULL),
                                                                                             (4, 700, 'bouteille', 120.00, '2030-01-01', NULL),
                                                                                             (5, 330, 'canette', 2.50, '2023-12-31', '2023-11-01');

-- Remplissage de la table Magasin
INSERT INTO Magasin (nom, adresse, dateFermeture) VALUES
                                                      ('Cave de Paris', '12 rue de la Paix, Paris, France', NULL),
                                                      ('Wine World', '45 High Street, London, UK', NULL),
                                                      ('Enoteca Roma', 'Via Condotti, 25, Rome, Italy', '2025-01-01');

-- ✅ Remplissage de la table MouvementStock (Adjusting Quantities)
-- ✅ Store **only positive values** for both Approvisionnement and Vente
INSERT INTO MouvementStock (idMagasin, idProduit, volume, recipient, date, quantite) VALUES
                                                                                         -- 🏪 Approvisionnements (Stock In)
                                                                                         (1, 1, 750, 'bouteille', '2024-01-10', 100),
                                                                                         (1, 2, 750, 'bouteille', '2024-01-15', 50),
                                                                                         (2, 3, 750, 'bouteille', '2024-01-20', 30),
                                                                                         (2, 5, 330, 'canette', '2023-12-01', 500),
                                                                                         (3, 4, 700, 'bouteille', '2023-11-25', 20),

                                                                                         -- 🛍 Ventes (Stock Out - stored as positive)
                                                                                         (1, 1, 750, 'bouteille', '2024-02-01', 80),
                                                                                         (1, 2, 750, 'bouteille', '2024-02-05', 30),
                                                                                         (2, 3, 750, 'bouteille', '2024-02-10', 20),
                                                                                         (2, 5, 330, 'canette', '2024-02-15', 400),
                                                                                         (3, 4, 700, 'bouteille', '2024-02-20', 10);

-- ✅ Remplissage de la table Vendeur
INSERT INTO Vendeur (idMagasin, nom, salaire, estActif) VALUES
                                                            (1, 'Alice Dupont', 2500.00, TRUE),
                                                            (2, 'John Smith', 2200.00, TRUE),
                                                            (3, 'Giulia Rossi', 2400.00, FALSE);

-- ✅ Remplissage de la table Client
INSERT INTO Client (nom, adresse, email, pointDeFidelite, anneeNaissance) VALUES
                                                                              ('Paul Morel', '34 avenue des Champs, Paris, France', 'paul.morel@example.com', 120, 1985),
                                                                              ('Anna Garcia', '23 Carrer Major, Barcelona, Spain', 'anna.garcia@example.com', 80, 1990),
                                                                              ('James Taylor', '56 Broadway, New York, USA', 'james.taylor@example.com', 200, 1980);

-- ✅ Approvisionnements
INSERT INTO Approvisionnement (idMouvementStock, dateCommande) VALUES
                                                                   (1, '2024-01-01'),
                                                                   (2, '2024-01-02'),
                                                                   (3, '2024-01-03'),
                                                                   (4, '2024-01-04'),
                                                                   (5, '2024-01-05');

-- ✅ Ventes (linked to sellers and clients)
INSERT INTO Vente (idMouvementStock, idVendeur, idClient) VALUES
                                                              (6, 1, 1),
                                                              (7, 1, 2),
                                                              (8, 2, 3),
                                                              (9, 2, 1),
                                                              (10, 3, 2);


-- ✅ Remplissage de la table Fournisseur
INSERT INTO Fournisseur (nom, adresse, numeroTelephone) VALUES
                                                            ('Distrib Vins France', '45 rue du Vin, Bordeaux, France', '+33 5 56 00 00 01'),
                                                            ('Champagne Select', '12 avenue de la Champagne, Reims, France', '+33 3 26 00 00 02'),
                                                            ('Global Spirits', '99 Whiskey Lane, Dublin, Ireland', '+353 1 00 00 03');

-- ✅ Remplissage de la table Approvisionnement_Fournisseur
INSERT INTO Approvisionnement_Fournisseur (idMouvementStock, idFournisseur) VALUES
                                                                                (4, 1),
                                                                                (5, 3);
