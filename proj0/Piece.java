public class Piece {
    private boolean isFire;
    private boolean isKing;
    private boolean hasCaptured;
    private int positionX;
    private int positionY;
    private Board board;
    private String type;

    public Piece(boolean isFire, Board b, int x, int y, String type){
        this.isFire = isFire;
        this.isKing = false;
        this.positionX = x;
        this.positionY = y;
        this.board = b;
        this.type = type;
    }

    public boolean isFire(){
        return isFire;
    }

    public int side(){
        if (isFire){
            return 0;
        }
        return 1;
    }

    public boolean isKing(){
        return isKing;
    }

    public boolean isBomb(){
        return type.equals("bomb");
    }

    public boolean isShield(){
        return type.equals("shield");
    }

    public void move(int x, int y){
        board.place(this, x, y);
        board.remove(positionX, positionY);
        if (Math.abs(positionX - x) == 2 && Math.abs(positionY - y) == 2){
            board.remove((positionX + x) / 2, (positionY + y) / 2);
            if (type == "bomb"){
                for (int i = -1; i < 2; i++){
                    for (int j = -1; j < 2; j++){
                        if (board.pieceAt(x+i,y+j) == null || board.pieceAt(x+i,y+j).isShield())
                            continue;
                        board.remove(x+i,y+j);
                    }
                }
            }
            hasCaptured = true;
        }
        if (isFire && y == 7)
            isKing = true;
        if (!isFire && y == 0)
            isKing = true;
        positionX = x;
        positionY = y;
    }

    public boolean hasCaptured(){
        return hasCaptured;
    }

    public void doneCapturing(){
        hasCaptured = false;
    }

}
