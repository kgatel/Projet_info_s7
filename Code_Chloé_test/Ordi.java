import javax.swing.JFrame;

public class Ordi extends Joueur {
	
	public Ordi(Couleur couleur, String pseudo) {
		super(couleur,pseudo);
	}
	
	public void tourOrdi(boolean tourBlanc,boolean peutMangerEnArriere, boolean obligerLesSauts) throws CloneNotSupportedException {
		
		Arbre arbre = creerArbreCoupPossible(this.getDamier(),peutMangerEnArriere);
		
		boolean afficherLesDamiers = true;
		
		if (afficherLesDamiers) {
			for (int indice=0;indice<arbre.getRacine().getSuccesseurs().size();indice++) {
				JFrame f = new JFrame(arbre.getRacine().getSuccesseurs(indice).getValeur().getName());
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(this.getDamier().getTAILLE(),this.getDamier().getTAILLE()+37);  //le +37 est nécessaire à l'affichage de la dernière ligne
				f.add(arbre.getRacine().getSuccesseurs(indice).getValeur());
				f.setVisible(true);
			}
		}
		
		
		
		//attendre(100000);
		int compteur=0,compteur2=0,compteur3=0;
		for (int j=0;j<this.getDamier().getTaille();j++) { 
			for (int i=0;i<this.getDamier().getTaille();i++) {
				if (this.getDamier().getCases()[i][j].getPiece()!=null) {
					if ((this.getDamier().getCases()[i][j].getPiece().getCouleur()==this.getCouleur())&&(compteur<1)) {
						this.Ajoue(i,j,tourBlanc,peutMangerEnArriere,obligerLesSauts);
						for (int jj=0;jj<this.getDamier().getTaille();jj++) { 
							for (int ii=0;ii<this.getDamier().getTaille();ii++) {
								if ((this.getDamier().getCases()[ii][jj].getPossibleClique())||(this.getDamier().getCases()[ii][jj].getSaut())&&(compteur2<1)) {
									compteur2++;
									this.Ajoue(ii,jj,tourBlanc,peutMangerEnArriere,obligerLesSauts);
									if (this.getDamier().getSautMultiple()) {
										//attendre(500);
									}
									while (this.getDamier().getSautMultiple()) {
										for (int jjj=0;jjj<this.getDamier().getTaille();jjj++) { 
											for (int iii=0;iii<this.getDamier().getTaille();iii++) {
												if (this.getDamier().getCases()[iii][jjj].getSaut()&&(compteur3<1)) {
													this.Ajoue(iii,jjj,tourBlanc,peutMangerEnArriere,obligerLesSauts);
													compteur3++;
												}
											}
										}
									}
									compteur++;
								}
								
							}
						}
					}
				}
			}
		}
	}

	public Coordonnees[] ListeDesCoupsPossibles(Piece piece, boolean peutMangerEnArriere, boolean sautMultiple) throws CloneNotSupportedException {
		Coordonnees[] res = new Coordonnees[(this.getDamier().getTaille()-1)*2];
		for (int k=0;k<(this.getDamier().getTaille()-1)*2;k++) {
			res[k]=null;
		}
		//Creation d'un damier et d'une pièce copies de test
		Damier damierTest = (Damier) piece.getDamier().clone();
		Piece pieceTest = new Piece(piece.getCouleur(),piece.getCoordonnees(),damierTest);		
		
		boolean tourBlanc=false;
		if (pieceTest.getCouleur()==Couleur.Blanc) {
			tourBlanc=true;
		}
		
		pieceTest.afficherDeplacement(tourBlanc, peutMangerEnArriere);
		
		int indice = 0;
		for (int i=0;i<damierTest.getTaille();i++) {
			for (int j=0;j<damierTest.getTaille();j++) {
				
				if ( ((damierTest.getCases()[i][j].getPossibleClique())&&(!sautMultiple))||(damierTest.getCases()[i][j].getSaut()) ) {
					Coordonnees c = new Coordonnees(i,j);
					res[indice]=c;
					indice++;
				}
				//remettre à neuf le damier à chaque fois
				damierTest.getCases()[i][j].setPossibleClique(false);
				damierTest.getCases()[i][j].setSaut(false);
				damierTest.getCases()[i][j].setClique(false);
				//
			}
		}
		
		return res;
	}
	
	private Arbre creerArbreCoupPossible(Damier damier, boolean peutMangerEnArriere) throws CloneNotSupportedException {
		
		//On travaille avec un copie du tableau de pièce pour ne pas changer les valeurs du vrai tableau
		Damier damierCopie = (Damier)this.getDamier().clone();
		TableauPiece piecesTemp = new TableauPiece(damierCopie,damierCopie.getTaille(),this.getCouleur());
		
		NoeudDame racine = new NoeudDame(damierCopie);
		//affectation de toutes les pièces
		for (int i=0;i<piecesTemp.getTailleTabPiece();i++) {
			if (this.getPieces(i)==null) {
				piecesTemp.setPiece(null,i);
			}else {
				piecesTemp.setPiece((Piece) this.getPieces(i).clone(), i);
			}
		}
		
		for (int i=0;i<piecesTemp.getTailleTabPiece();i++) {
			if (piecesTemp.getPiece(i)!=null) {
				this.genererArbreParPiece(racine,piecesTemp,i,damierCopie, true, false,peutMangerEnArriere);
			}
		}
		
		Arbre res = new Arbre(racine);
		return res;
	}
	
	private void genererArbreParPiece(NoeudDame racine,TableauPiece piecesTemp,int i, Damier damierCopie, boolean premierCoup,boolean sautMultiple,boolean peutMangerEnArriere) throws CloneNotSupportedException {
		boolean premierCoupCopie=premierCoup;
		if (sautMultiple) {
			damierCopie.getCase(piecesTemp.getPiece(i).getCoordonnees().X(),piecesTemp.getPiece(i).getCoordonnees().Y()).setSaut(false);
		}
		if ((sautMultiple)||(premierCoupCopie)){
			
			Coordonnees[] listeDeCoupPossible = this.ListeDesCoupsPossibles(piecesTemp.getPiece(i), peutMangerEnArriere,sautMultiple);
			int indice=0;
			while (listeDeCoupPossible[indice]!=null) {
				
				Damier damierIndice = null;
				TableauPiece piecesIndices = null;
				
				damierIndice = (Damier)damierCopie.clone();
				damierIndice.setName(""+racine.getSuccesseurs().size());
				piecesIndices = (TableauPiece)piecesTemp.clone();
				if (this.getCouleur()==Couleur.Blanc) {
					damierIndice.setPiecesBlanches(piecesIndices);
				}else {
					damierIndice.setPiecesNoires(piecesIndices);
				}
				piecesIndices.setDamier(damierIndice);
				
				boolean pion=(damierIndice.getPiece(piecesIndices.getPiece(i).getCoordonnees().X(),piecesIndices.getPiece(i).getCoordonnees().Y()) instanceof Pion);
				boolean b=false;
				boolean tourBlanc=false;
				if (this.getCouleur()==Couleur.Blanc) {
					tourBlanc=true;
				}
				
				//garder en mémoire les coordonnees de la pièce avant qu'elle se déplace
				int x = piecesIndices.getPiece(i).getCoordonnees().X();
				int y = piecesIndices.getPiece(i).getCoordonnees().Y();
				
				piecesIndices.deplacer(x, y, listeDeCoupPossible[indice].X(), listeDeCoupPossible[indice].Y(), tourBlanc);
				
				//si une pièce a été mangée il faut la supprimer
				
				Coordonnees c = piecesIndices.pieceMangeeLorsDunSaut(x,y,piecesIndices.getPiece(i).getCoordonnees().X(),piecesIndices.getPiece(i).getCoordonnees().Y(),tourBlanc);	//savoir s'il y a eu une pièce mangée ou non
				if (c.X()!=-1) {		//il y a eu une pièce mangée
					damierIndice.getCase(c.X(),c.Y()).setPiece(null);  //enlever la pièce mangée
					if (tourBlanc) {
						damierIndice.getPiecesNoires().setPiece(null, damierIndice.getPiecesNoires().trouverIndice(c));
					}else {
						damierIndice.getPiecesBlanches().setPiece(null, damierIndice.getPiecesBlanches().trouverIndice(c));
					}
					if (!( (pion)&&(damierIndice.getCase(piecesIndices.getPiece(i).getCoordonnees().X(),piecesIndices.getPiece(i).getCoordonnees().Y()).getPiece() instanceof Reine) )){   //vérifier qu'il ne peut pas continuer à manger s'il vient d'obtenir une reine
						b = damierIndice.getCase(piecesIndices.getPiece(i).getCoordonnees().X(),piecesIndices.getPiece(i).getCoordonnees().Y()).getPiece().sautPossible(tourBlanc,peutMangerEnArriere,true);		//si b=true alors le joueur peut continuer à sauter
					}
				}
				
				if (this.getCouleur()==Couleur.Blanc) {
					damierIndice.setPiecesBlanches(piecesIndices);
				}else {
					damierIndice.setPiecesNoires(piecesIndices);
				}			
				
				if (b) {

					this.genererArbreParPiece(racine, piecesIndices, i, damierIndice,false, true, peutMangerEnArriere);
					
				}else {
					damierIndice.getCase(piecesIndices.getPiece(i).getCoordonnees().X(), piecesIndices.getPiece(i).getCoordonnees().Y()).setSaut(false);
					NoeudDame nouveauNoeud = new NoeudDame(damierIndice);
					racine.ajouterSuccesseur(nouveauNoeud);
				}
				indice++;
			}
			
		}
	}
	
/*	private Damier algoMiniMax(Arbre arbre){ //besoin de la profondeur, noeud, créer la classe feuille ?
		int val=0;
		NoeudDame N;
		if ((profondeur==0) | (noeud == N)) {
			return (arbre.N.Heuristique);}
		if (true){          //faudra remplacer si c'est l'IA qui est en train de jouer ou le joueur
			return (-100000);
		for(i=0,i<arbre.N.getSuccesseurs.size(),i++){
			val= max(val, algoMiniMax(arbre.N.getSuccesseurs(i), profondeur-1, false));
			return(val);}
			else {
		val= 100000;}
		for(i=0,i<arbre.N.getSuccesseurs.size(),i++){
			val= min(val, algoMiniMax(arbre.N.getSuccesseurs(i), profondeur-1, true));
			return(val);
		}
		}
		}
*/		
	
	}
