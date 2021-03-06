
public class TableauPiece implements Cloneable{

	//Attributs
	private Damier damier;
	private Piece [] piece;
	private int tailleTabPiece;
	private Couleur couleur;
	
	
	public TableauPiece(Damier damier,int taille,Couleur couleur){
		this.damier=damier;
		this.piece=this.tableauPiece(taille, couleur);
		this.tailleTabPiece=(2+(taille-4)/2)*((taille-4)/2+1);
		this.couleur=couleur;
	}
	
	
	//Constructeurs
	
	public int getTailleTabPiece() {
		return tailleTabPiece;
	}

	public void setTailleTabPiece(int tailleTabPiece) {
		this.tailleTabPiece = tailleTabPiece;
	}	
	
	public Damier getDamier() {
		return damier;
	}

	public void setDamier(Damier damier) {
		this.damier = damier;
		for (int i=0;i<tailleTabPiece;i++) {
			if (this.piece[i]!=null) {
				this.piece[i].setDamier(damier);
			}
		}
	}
	
	public Couleur getCouleur() {
		return couleur;
	}

	public void setCouleur(Couleur couleur) {
		this.couleur = couleur;
	}
	
	public Piece[] getPieces() {
		return this.piece;
	}
	
	public void setPieces(Piece[] piece) {
		this.piece=piece;
	}
	
	public Piece getPiece(int i) {
		return this.piece[i];
	}

	public void setPiece(Piece piece,int i) {
		this.piece[i] = piece;
	}


	//Méthodes
	private Piece[] tableauPiece(int taille,Couleur couleur) {
		Piece[] res = new Piece[(2+(taille-4)/2)*((taille-4)/2+1)];
		int compteurB=0,compteurN=0;
		
		for (int j=taille-1;j>=0;j--) {
			for (int i=0;i<taille;i++) {
				if ( ((i+j)%2)==1) {
					Coordonnees c = new Coordonnees(i,j);
					if ((j>=taille/2+1)&&(couleur==Couleur.Blanc)) {
						res[compteurB]=new Pion(Couleur.Blanc,c,damier);
						compteurB++;
					}
					if ((j<=taille/2-2)&&(couleur==Couleur.Noir)) {
						res[compteurN]=new Pion(Couleur.Noir,c,damier);
						compteurN++;
					}
				}
			}
		}
		return res;
	}
	
	
	public int trouverIndice(Coordonnees c) {
		int res = -1;
		int i=0;
		while ((res==-1)&&(i<this.tailleTabPiece)) {
			if (piece[i]!=null) {
				if ((this.piece[i].getC().X()==c.X())&&(this.piece[i].getC().Y()==c.Y())) {
					res=i;
				}
			}
			i++;
		}
		return res;
	}

	public void deplacer(int i, int j, int x, int y, boolean tourBlanc) { //déplacer la pièce de (i,j) à (x,y)		
		boolean reine = (this.getDamier().getCases()[i][j].getPiece() instanceof Reine);
		Coordonnees c1 = new Coordonnees(i,j);
		Coordonnees c2 = new Coordonnees(x,y);
		
		int indice = trouverIndice(c1);
		if (tourBlanc) {	//piece blanche
			if ((y==0)||(reine)) {
				this.setPiece(new Reine(Couleur.Blanc,c2,damier), indice);
				this.getDamier().getCases()[x][y].setPiece(new Reine(Couleur.Blanc,c2,damier));
				//on vient d'obtenir une reine
			}
			else {
				this.setPiece(new Pion(Couleur.Blanc,c2,damier), indice);
				this.getDamier().getCases()[x][y].setPiece(new Pion(Couleur.Blanc,c2,damier));
			}
		}
		else {	//piece noire
			if ((y==this.getDamier().getTaille()-1)||(reine)) {
				this.setPiece(new Reine(Couleur.Noir,c2,damier), indice);
				this.getDamier().getCases()[x][y].setPiece(new Reine(Couleur.Noir,c2,damier));
				//on vient d'obtenir une reine
			}
			else {
				this.setPiece(new Pion(Couleur.Noir,c2,damier), indice);
				this.getDamier().getCases()[x][y].setPiece(new Pion(Couleur.Noir,c2,damier));
			}
		}
		
		for (int ii=0;ii<this.damier.getTaille();ii++) {
			for (int jj=0;jj<this.damier.getTaille();jj++) {
				if (this.getDamier().getCases()[ii][jj].getPossibleClique()) {		//rénitialiser toutes les cases sur lesquelles le pion pouvait bouger
					this.getDamier().getCases()[ii][jj].setPossibleClique(false);
				}
			}
		}
		this.getDamier().getCases()[i][j].setPiece(null);
	}
	
	public Coordonnees pieceMangeeLorsDunSaut(int x, int y,int i,int j,boolean tourBlanc) { //donne les coordonnées de la pièce mangée
		Coordonnees c = new Coordonnees();
		int delta=abs(y-j);
		if (delta>=2) {
			if (y-j<0) {
				if (x-i>0) {		//diagonale haute droite
					int k=1;
					while ((i+k<damier.getTaille())&&(j-k>0)&&(damier.getCases()[i+k][j-k].getPiece()==null)) {
						k++;
					}
					if ((i+k<damier.getTaille())&&(j-k>0)) {
						if ( ((damier.getCases()[i+k][j-k].getPiece().getCouleur()==Couleur.Blanc)&&(!tourBlanc)) || ((damier.getCases()[i+k][j-k].getPiece().getCouleur()==Couleur.Noir)&&(tourBlanc)) ) {
							c.setX(i+k);
							c.setY(j-k);
						}
					}
					
				}
				if (x-i<0)	{		//diagonale haute gauche
					int k=1;
					while ((i-k>0)&&(j-k>0)&&(damier.getCases()[i-k][j-k].getPiece()==null)) {
						k++;
					}
					if ((i-k>0)&&(j-k>0)) {
						if ( ((damier.getCases()[i-k][j-k].getPiece().getCouleur()==Couleur.Blanc)&&(!tourBlanc)) || ((damier.getCases()[i-k][j-k].getPiece().getCouleur()==Couleur.Noir)&&(tourBlanc)) ) {
							c.setX(i-k);
							c.setY(j-k);
						}
					}
				}
			}
			else {  //y-j>0
				if (x-i>0) {		//diagonale basse droite
					int k=1;
					while ((i+k<damier.getTaille()-1)&&(j+k<damier.getTaille()-1)&&(damier.getCases()[i+k][j+k].getPiece()==null)) {
						k++;
					}
					if ((i+k<damier.getTaille()-1)&&(j+k<damier.getTaille()-1)) {
						if ( ((damier.getCases()[i+k][j+k].getPiece().getCouleur()==Couleur.Blanc)&&(!tourBlanc)) || ((damier.getCases()[i+k][j+k].getPiece().getCouleur()==Couleur.Noir)&&(tourBlanc)) ) {
							c.setX(i+k);
							c.setY(j+k);
						}
					}
				}
				if (x-i<0)	{		//diagonale basse gauche
					int k=1;
					while ((i-k>0)&&(j+k<damier.getTaille()-1)&&(damier.getCases()[i-k][j+k].getPiece()==null)) {
						k++;
					}
					if ((i-k>0)&&(j+k<damier.getTaille()-1)) {
						if ( ((damier.getCases()[i-k][j+k].getPiece().getCouleur()==Couleur.Blanc)&&(!tourBlanc)) || ((damier.getCases()[i-k][j+k].getPiece().getCouleur()==Couleur.Noir)&&(tourBlanc)) ) {
							c.setX(i-k);
							c.setY(j+k);
						}
					}
				}
			}
		}
		return c;
	}
	
	public Object clone(){
	       try {
	           TableauPiece tmp = (TableauPiece) super.clone();
	           Piece[] pieceTmp = new Piece[this.tailleTabPiece];
	           for (int i=0;i<this.tailleTabPiece;i++) {
	        	   if (this.getPiece(i)==null) {
	        		   pieceTmp[i]=null;
	        	   }else {
	        		   if (this.getPiece(i) instanceof Pion) {
	        			   pieceTmp[i]=new Pion(this.couleur,this.getPiece(i).getC(),this.damier);
	        		   }else{
	        			   pieceTmp[i]=new Reine(this.couleur,this.getPiece(i).getC(),this.damier);
	        		   }
	        	   }
	        	   
	           }
	           tmp.setPieces(pieceTmp);
	           return tmp;
	        }
	        catch (CloneNotSupportedException e)
	           {throw new InternalError(); }
	 }
	
	private int abs(int a) {
		if (a>=0) {
			return a;
		}
		else {
			return -a;
		}
	}
	
}
