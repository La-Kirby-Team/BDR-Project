CREATE TABLE IF NOT EXISTS Provenance(
    id SERIAL,
    pays VARCHAR(80),
    region VARCHAR(80),
    producteur VARCHAR(80),
    CONSTRAINT PK_Provenance PRIMARY KEY (id)
);

--CREATE TYPE typeRecipient AS ENUM ('bouteille', 'canette');

CREATE TABLE IF NOT EXISTS Produit(
    id SERIAL,
    idProduit INTEGER NOT NULL,
    nom VARCHAR(80) NOT NULL,
    tauxAlcool DOUBLE PRECISION NOT NULL,
    CONSTRAINT PK_Produit PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS Article(
    idProduit INTEGER,
    volume INTEGER,
    recipient typeRecipient,
    prix DOUBLE PRECISION NOT NULL,
    datePeremption DATE NOT NULL,
    dateFinDeVente DATE,
    CONSTRAINT PK_Article PRIMARY KEY (idProduit, volume, recipient),
    CONSTRAINT FK_Article_Produit FOREIGN KEY (idProduit) REFERENCES Produit(id) ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS Magasin(
    id SERIAL,
    nom VARCHAR(80) NOT NULL,
    adresse VARCHAR(350) NOT NULL,
    dateFermeture DATE,
    CONSTRAINT PK_Magasin PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS MouvementStock(
    id SERIAL,
    idMagasin INTEGER NOT NULL,
    idProduit INTEGER NOT NULL,
    volume INTEGER NOT NULL,
    recipient typeRecipient NOT NULL,
    date DATE,
    quantite INTEGER,
    CONSTRAINT PK_MouvementStock PRIMARY KEY (id),
    CONSTRAINT FK_MouvementStock_Magasin FOREIGN KEY (idMagasin) REFERENCES Magasin(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT FK_MouvementStock_Article FOREIGN KEY (idProduit, volume, recipient) REFERENCES Article(idProduit, volume, recipient) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Vendeur(
    id SERIAL,
    idMagasin INTEGER NOT NULL,
    nom VARCHAR(80),
    salaire DOUBLE PRECISION,
    estActif BOOL NOT NULL,
    CONSTRAINT PK_Vendeur PRIMARY KEY (id),
    CONSTRAINT FK_Vendeur_Magasin FOREIGN KEY (idMagasin) REFERENCES Magasin(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Client(
    id SERIAL,
    nom VARCHAR(80),
    adresse VARCHAR(150),
    email VARCHAR(100),
    pointDeFidelite INTEGER,
    anneeNaissance INTEGER,
    CONSTRAINT PK_Client PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS Vente(
    idMouvementStock INTEGER,
    idVendeur INTEGER NOT NULL,
    idClient INTEGER NOT NULL,
    CONSTRAINT PK_Vente PRIMARY KEY (idMouvementStock),
    CONSTRAINT FK_Vente_MouvementStock FOREIGN KEY (idMouvementStock) REFERENCES MouvementStock(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT FK_Vente_Vendeur FOREIGN KEY (idVendeur) REFERENCES Vendeur(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT FK_Vente_Client FOREIGN KEY (idClient) REFERENCES Client(id) ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS Approvisionnement(
    idMouvementStock INTEGER,
    dateCommande DATE NOT NULL,
    CONSTRAINT PK_Approvisionnement PRIMARY KEY (idMouvementStock),
    CONSTRAINT FK_Approvisionnement_Approvisionnement FOREIGN KEY (idMouvementStock) REFERENCES MouvementStock(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT check_DateCommande CHECK (dateCommande < CURRENT_DATE)
);

CREATE TABLE IF NOT EXISTS Fournisseur(
    id SERIAL,
    nom VARCHAR(80),
    adresse VARCHAR(150),
    numeroTelephone VARCHAR(30),
    CONSTRAINT PK_Fournisseur PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS Approvisionnement_Fournisseur(
    idMouvementStock INTEGER,
    idFournisseur INTEGER,
    CONSTRAINT PK_Approvisionnement_Fournisseur PRIMARY KEY (idMouvementStock, idFournisseur),
    CONSTRAINT FK_Approvisionnement_Fournisseur_idMouvementStock FOREIGN KEY (idMouvementStock) REFERENCES Approvisionnement(idMouvementStock) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT FK_Approvisionnement_Fournisseur_idFournisseur FOREIGN KEY (idFournisseur) REFERENCES Fournisseur(id) ON UPDATE CASCADE ON DELETE CASCADE
);