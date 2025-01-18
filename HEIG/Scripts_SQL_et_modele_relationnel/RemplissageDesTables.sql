-- Remplissage de la table Provenance
INSERT INTO Provenance (pays, region, producteur) VALUES
('France', 'Bordeaux', 'Château Margaux'),
('Italie', 'Toscane', 'Antinori'),
('Espagne', 'Ribera del Duero', 'Bodega Vega Sicilia'),
('USA', 'Napa Valley', 'Robert Mondavi'),
('France', 'Champagne', 'Moët & Chandon');

-- Remplissage de la table Produit
INSERT INTO Produit (idProduit, nom, tauxAlcool) VALUES
(1, 'Vin Rouge Bordeaux', 13.5),
(2, 'Vin Blanc Chardonnay', 12.0),
(3, 'Champagne Brut', 12.5),
(4, 'Whisky Single Malt', 40.0),
(5, 'Bière Blonde', 5.0);

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

-- Remplissage de la table MouvementStock
INSERT INTO MouvementStock (idMagasin, idProduit, volume, recipient, date, quantite) VALUES
(1, 1, 750, 'bouteille', '2024-01-10', 100),
(1, 2, 750, 'bouteille', '2024-01-15', 50),
(2, 3, 750, 'bouteille', '2024-01-20', 30),
(2, 5, 330, 'canette', '2023-12-01', 500),
(3, 4, 700, 'bouteille', '2023-11-25', 20);

-- Remplissage de la table Vendeur
INSERT INTO Vendeur (idMagasin, nom, salaire, estActif) VALUES
(1, 'Alice Dupont', 2500.00, TRUE),
(2, 'John Smith', 2200.00, TRUE),
(3, 'Giulia Rossi', 2400.00, FALSE);

-- Remplissage de la table Client
INSERT INTO Client (nom, adresse, email, pointDeFidelite, anneeNaissance) VALUES
('Paul Morel', '34 avenue des Champs, Paris, France', 'paul.morel@example.com', 120, 1985),
('Anna Garcia', '23 Carrer Major, Barcelona, Spain', 'anna.garcia@example.com', 80, 1990),
('James Taylor', '56 Broadway, New York, USA', 'james.taylor@example.com', 200, 1980);

-- Remplissage de la table Vente
INSERT INTO Vente (idMouvementStock, idVendeur, idClient) VALUES
(1, 1, 1),
(2, 1, 2),
(3, 2, 3);

-- Remplissage de la table Approvisionnement
INSERT INTO Approvisionnement (idMouvementStock, dateCommande) VALUES
(4, '2023-11-01'),
(5, '2023-10-15');

-- Remplissage de la table Fournisseur
INSERT INTO Fournisseur (nom, adresse, numeroTelephone) VALUES
('Distrib Vins France', '45 rue du Vin, Bordeaux, France', '+33 5 56 00 00 01'),
('Champagne Select', '12 avenue de la Champagne, Reims, France', '+33 3 26 00 00 02'),
('Global Spirits', '99 Whiskey Lane, Dublin, Ireland', '+353 1 00 00 03');

-- Remplissage de la table Approvisionnement_Fournisseur
INSERT INTO Approvisionnement_Fournisseur (idMouvementStock, idFournisseur) VALUES
(4, 1),
(5, 3);
