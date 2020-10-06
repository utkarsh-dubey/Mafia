package com.company;

import java.util.*;

abstract class Player{
    protected boolean dead=false;
    protected boolean flag=false,checkFlag=false;
    protected boolean detectiveFlag=false,healerFlag=false;
    public boolean checkDead(){
        return dead;
    }

    public void kill(){
        dead=true;
    }
    public void printAlive(ArrayList<Player> players,int roundNumber){
        System.out.println("Round"+roundNumber);
        int count=0;
        for(Player i:players){
            if(!i.checkDead()){
                count++;
            }
        }
        System.out.print(count+" Players are remaining: ");
        for(Player i:players){
            if(!i.checkDead()) {
                System.out.print(i.getPlayerNumber() + ", ");
            }
        }
        int checkCount=0,checkCount2=0;
        for(Player i:players){
            if(i instanceof Detective && !i.checkDead()){
                checkCount++;
            }
            if(i instanceof Healer && !i.checkDead()){
                checkCount2++;
            }
        }
        if(checkCount==0){
            detectiveFlag=true;
        }
        if(checkCount2==0){
            healerFlag=true;
        }
        System.out.println(" are alive.");
    }
    abstract String getType();
    abstract float getHp();
    abstract void setHp(float hp);
    abstract String getPlayerNumber();
    @Override
    public boolean equals(Object a){
        if(a==null || getClass()!=a.getClass()){
            return false;
        }
        return this == a;
    }
    abstract void playGame(ArrayList<Player> players);

    public boolean checkGame(ArrayList<Player> players){
        int mafias=0,others=0;
        for(Player i:players){
            if(i instanceof Mafia && !i.checkDead()){
                mafias++;
            }
            else if(!(i instanceof Mafia) && !i.checkDead()  ){
                others++;
            }
        }
        if(mafias==others){
            flag=true;
            return false;

        }
        return mafias != 0;
    }
    public void automateMafia(ArrayList<Player> players,int target,int detectiveTest,int healerchoice){

        int mafiaNumber=0;float totalMafiaHp=0;
        float damageToGive=players.get(target-1).getHp();
        for(Player p:players){
            if(p instanceof Mafia &&!p.checkDead()){
                mafiaNumber++;
                totalMafiaHp+=p.getHp();
            }
        }
        float hpDecrease=players.get(target-1).getHp()/mafiaNumber;
        if(totalMafiaHp>=players.get(target-1).getHp()){
            players.get(target-1).setHp(players.get(target-1).getHp());
        }
        else{
            players.get(target-1).setHp(totalMafiaHp);
        }
        while(damageToGive>0){
            if(mafiaNumber==0){
                break;
            }
            hpDecrease=damageToGive/mafiaNumber;
            mafiaNumber=0;
            for(Player p:players){
                if(p instanceof Mafia){
                    if(p.getHp()>hpDecrease){
                        p.setHp(hpDecrease);
                        damageToGive-=hpDecrease;
                        mafiaNumber+=1;
                    }
                    else{
                        damageToGive-=p.getHp();
                        p.setHp(p.getHp());

                    }
                }
            }
        }
        while(players.get(healerchoice).checkDead()){
            healerchoice=Main.rand.nextInt(players.size());
        }
        if(!healerFlag) {
            players.get(healerchoice).setHp(-500);
        }
        if(players.get(target-1).getHp()==0){
            System.out.println(players.get(target-1).getPlayerNumber()+" is dead");
            players.get(target-1).kill();
            if(!checkGame(players)){
                checkFlag=true;
            }
        }
        else{
            System.out.println("No one died");
        }
    }

    public void voteOut(ArrayList<Player> players,int voteout,int detecticeTest){
        if(players.get(detecticeTest) instanceof Mafia&&!checkFlag){
            System.out.println(((Mafia) players.get(detecticeTest)).getPlayerNumber()+" was voted out");
            players.get(detecticeTest).kill();
        }
        else {
            if(!checkDead()) {
                System.out.println("Select a person to vote out:");
                int votetemp = Main.in.nextInt();
                if (players.get(votetemp - 1).checkDead()) {
                    System.out.println("Please choose a different person to voteout, as the chosen person is dead ");
                    votetemp = Main.in.nextInt();
                }
            }

            voteout++;
            System.out.println("Player" + voteout + " has been voted out");
            players.get(voteout - 1).kill();
        }
    }
    public void end(ArrayList<Player> players){
        System.out.println("Game Over");
        if(flag){
            System.out.println("The Mafias have won");
        }
        else{
            System.out.println("The Mafias have lost");
        }
        for(Player p:players){
            if(p instanceof Mafia){
                System.out.print(p.getPlayerNumber()+", ");
            }
        }
        System.out.println(" were mafias.");
        for(Player p:players){
            if(p instanceof Detective){
                System.out.print(p.getPlayerNumber()+", ");
            }
        }
        System.out.println(" were detectives.");
        for(Player p:players){
            if(p instanceof Healer){
                System.out.print(p.getPlayerNumber()+", ");
            }
        }
        System.out.println(" were healers.");
        for(Player p:players){
            if(p instanceof Commoner){
                System.out.print(p.getPlayerNumber()+", ");
            }
        }
        System.out.println(" were commoners.");
    }

    public static Comparator<Player> ordering = new Comparator<Player>() {
        @Override
        public int compare(Player o1, Player o2) {
            return Integer.parseInt(o1.getPlayerNumber().substring(o1.getPlayerNumber().indexOf('r')+1))-Integer.parseInt(o2.getPlayerNumber().substring(o2.getPlayerNumber().indexOf('r')+1));
        }
    };
}

class Mafia extends Player{

    final private String playerNumber;
    private float hp;

    Mafia(){
        this.hp=2500;
        playerNumber=" ";
    }

    Mafia(String playerNumber){
        this.hp=2500;
        this.playerNumber=playerNumber;
    }

    String getPlayerNumber(){
        return playerNumber;
    }
    float getHp(){
        return hp;
    }
    void setHp(float hp){
        this.hp-=hp;

    }
    public String getType(){
        return "Mafia";
    }

    public void playGame(ArrayList<Player> players){
        int i=1;
        while(super.checkGame(players)){
            super.printAlive(players,i);
            int target;
            if(!players.get(Integer.parseInt(playerNumber.substring(playerNumber.indexOf('r')+1))-1).checkDead()) {
                System.out.println("Choose a target: ");
                target = Main.in.nextInt();
                if(players.get(target-1) instanceof Mafia || players.get(target-1).checkFlag){
                    System.out.println("Please choose target ( Either you have chosen mafia or the chosen person is dead) ");
                    target=Main.in.nextInt();
                }
            }
            else{

                System.out.println("You are dead, watch the game");
                target=Main.rand.nextInt(players.size());
                while(players.get(target).checkDead()){
                    target=Main.rand.nextInt(players.size());
                }
                target++;
            }
            System.out.println("Detectives have chosen a player to test");
            System.out.println("Healers have chosen someone to heal");
            System.out.println("--End of actions--");
            int detecticeTest=Main.rand.nextInt(players.size());
            while(players.get(detecticeTest).checkDead()){
                detecticeTest=Main.rand.nextInt(players.size());
            }
            int healerchoice=Main.rand.nextInt(players.size());
            while(players.get(healerchoice).checkDead()){
                healerchoice=Main.rand.nextInt(players.size());
            }
            super.automateMafia(players,target,detecticeTest,healerchoice);
            if(checkFlag){
                break;
            }
            int voteout=Main.rand.nextInt(players.size());
            while(players.get(voteout).checkDead()){
                voteout=Main.rand.nextInt(players.size());
            }
            super.voteOut(players,voteout,detecticeTest);
            System.out.println("--End of Round "+i+"--");
            i++;
        }
        super.end(players);
    }
}

class Detective extends Player{

    private float hp;
    final private String playerNumber;
    Detective(){
        this.hp=800;
        playerNumber=" ";
    }

    Detective(String playerNumber){
        this.hp=800;
        this.playerNumber=playerNumber;
    }

    String getPlayerNumber(){
        return playerNumber;
    }
    float getHp(){
        return hp;
    }
    void setHp(float hp){
        this.hp-=hp;

    }
    String getType(){
        return "Detective";
    }

    public void playGame(ArrayList<Player> players){
        int i=1;
        while(super.checkGame(players)){
            super.printAlive(players,i);
            System.out.println("Mafias have chosen their target");
            int detectiveTest;
            if(!players.get(Integer.parseInt(playerNumber.substring(playerNumber.indexOf('r')+1))-1).checkDead()) {
                System.out.println("Choose a person to test: ");
                detectiveTest = Main.in.nextInt();
                detectiveTest--;
                if(players.get(detectiveTest) instanceof Detective|| players.get(detectiveTest).checkFlag){
                    System.out.println("Please choose different player to test ( Either you have chosen a detective or the chosen person is dead) ");
                    detectiveTest=Main.in.nextInt();
                }
            }
            else{
                System.out.println("You are dead, watch the game");
                detectiveTest=Main.rand.nextInt(players.size());
                while(players.get(detectiveTest).checkDead()){
                    detectiveTest=Main.rand.nextInt(players.size());

                }
            }
            if(!super.detectiveFlag) {
                if (players.get(detectiveTest) instanceof Mafia) {
                    System.out.println(players.get(detectiveTest).getPlayerNumber() + " is a Mafia");
                } else {
                    System.out.println(players.get(detectiveTest).getPlayerNumber() + " is not a Mafia");
                }
            }
            System.out.println("Healers have chosen someone to heal");
            System.out.println("--End of actions--");
            int target=Main.rand.nextInt(players.size());
            while(players.get(target).checkDead()){
                target=Main.rand.nextInt(players.size());
            }
            target++;
            int healerchoice=Main.rand.nextInt(players.size());
            while(players.get(healerchoice).checkDead()){
                healerchoice=Main.rand.nextInt(players.size());
            }
            super.automateMafia(players,target,detectiveTest,healerchoice);
            if(checkFlag){
                break;
            }
            int voteout=Main.rand.nextInt(players.size());
            while(players.get(voteout).checkDead()){
                voteout=Main.rand.nextInt(players.size());
            }
            super.voteOut(players,voteout,detectiveTest);
            System.out.println("--End of Round "+i+"--");
            i++;
        }
        super.end(players);
    }


}

class Healer extends Player{

    private float hp;
    final private String playerNumber;

    Healer(){
        this.hp=800;
        playerNumber=" ";
    }
    Healer(String playerNumber){
        this.playerNumber=playerNumber;
        this.hp=800;
    }

    String getPlayerNumber(){
        return playerNumber;
    }
    float getHp(){
        return hp;
    }
    void setHp(float hp){
        this.hp-=hp;

    }
    String getType(){
        return "Healer";
    }

    public void playGame(ArrayList<Player> players){
        int i=1;
        while(super.checkGame(players)){
            super.printAlive(players,i);
            System.out.println("Mafias have chosen their target");
            System.out.println("Detectives have chosen a player to test");
            int healerchoice;
            if(!players.get(Integer.parseInt(playerNumber.substring(playerNumber.indexOf('r')+1))-1).checkDead()) {
                System.out.println("Choose a person to heal:");
                healerchoice = Main.in.nextInt();
                healerchoice--;
                if(players.get(healerchoice).checkFlag){
                    System.out.println("Please choose a different person to heal, as the chosen person is dead ");
                    healerchoice=Main.in.nextInt();
                }
            }
            else{
                System.out.println("You are dead, watch the game");
                healerchoice=Main.rand.nextInt(players.size());
                while(players.get(healerchoice).checkDead()){
                    healerchoice=Main.rand.nextInt(players.size());

                }
            }
            int detectiveTest=Main.rand.nextInt(players.size());
            while(players.get(detectiveTest).checkDead()){
                detectiveTest=Main.rand.nextInt(players.size());
            }


            System.out.println("--End of actions--");
            int target=Main.rand.nextInt(players.size());
            while(players.get(target).checkDead()){
                target=Main.rand.nextInt(players.size());
            }
            target++;
            super.automateMafia(players,target,detectiveTest,healerchoice);
            if(checkFlag){
                break;
            }
            int voteout=Main.rand.nextInt(players.size());
            while(players.get(voteout).checkDead()){
                voteout=Main.rand.nextInt(players.size());
            }
            super.voteOut(players,voteout,detectiveTest);
            System.out.println("--End of Round "+i+"--");
            i++;
        }

        super.end(players);
    }

}

class Commoner extends Player{

    private float hp;
    final private String playerNumber;

    Commoner(){
        this.hp=1000;
        playerNumber=" ";
    }
    Commoner(String playerNumber){
        this.hp=1000;
        this.playerNumber=playerNumber;
    }

    String getPlayerNumber(){
        return playerNumber;
    }
    float getHp(){
        return hp;
    }
    void setHp(float hp){
        this.hp-=hp;

    }
    String getType(){
        return "Commoner";
    }

    public void playGame(ArrayList<Player> players){
        int i=1;
        while(super.checkGame(players)){
            super.printAlive(players,i);
            System.out.println("Mafias have chosen their target");
            System.out.println("Detectives have chosen a player to test");
            System.out.println("Healers have chosen a player to  heal");
            int healerchoice=Main.rand.nextInt(players.size());
            while(players.get(healerchoice).checkDead()){
                healerchoice=Main.rand.nextInt(players.size());

            }

            int detectiveTest=Main.rand.nextInt(players.size());
            while(players.get(detectiveTest).checkDead()){
                detectiveTest=Main.rand.nextInt(players.size());
            }


            System.out.println("--End of actions--");
            int target=Main.rand.nextInt(players.size());
            while(players.get(target).checkDead()){
                target=Main.rand.nextInt(players.size());
            }
            target++;

            super.automateMafia(players,target,detectiveTest,healerchoice);
            if(checkFlag){
                break;
            }
            int voteout=Main.rand.nextInt(players.size());
            while(players.get(voteout).checkDead()){
                voteout=Main.rand.nextInt(players.size());
            }
            super.voteOut(players,voteout,detectiveTest);
            System.out.println("--End of Round "+i+"--");
            i++;
        }
        super.end(players);
    }
}


class User<A>{
    A user;
    User(A user){
        this.user=user;
    }

    public ArrayList<Player> initiate(int n){

        ArrayList<Player> players=new ArrayList<>();

        boolean a[] =new boolean[n+1];

        int ntemp=n;
        int i=0;
        int num;
        while(i<n/5){
            num=Main.rand.nextInt(n)+1;
            while(a[num]){
                num=Main.rand.nextInt(n)+1;
            }
            a[num]=true;
            players.add(new Mafia("Player"+num));
            if(user instanceof Mafia){
                this.user=(A)players.get(players.size()-1);
            }
            i++;
        }
        ntemp-=(i);
        i=0;
        while(i<n/5){
            num=Main.rand.nextInt(n)+1;
            while(a[num]){
                num=Main.rand.nextInt(n)+1;
            }
            a[num]=true;
            players.add(new Detective("Player"+num));
            if(user instanceof Detective){
                this.user=(A)players.get(players.size()-1);
            }
            i++;
        }
        ntemp-=(i);
        i=0;
        while(i<Math.max(1,n/10)){
            num=Main.rand.nextInt(n)+1;
            while(a[num]){
                num=Main.rand.nextInt(n)+1;
            }
            a[num]=true;
            players.add(new Healer("Player"+num));
            if(user instanceof Healer){
                this.user=(A)players.get(players.size()-1);
            }
            i++;
        }
        ntemp-=(i);
        i=0;
        while(i<ntemp){
            num=Main.rand.nextInt(n)+1;
            while(a[num]){
                num=Main.rand.nextInt(n)+1;
            }
            a[num]=true;
            players.add(new Commoner("Player"+num));
            if(user instanceof Commoner){
                this.user=(A)players.get(players.size()-1);
            }
            i++;
        }

        return players;
    }

    public A getUser(){
        return this.user;
    }
}



public class Main {

    static Random rand=new Random();
    static Scanner in=new Scanner(System.in);

    public static void main(String[] args) {


	    ArrayList<Player> players;
	    User<Player> userObject;
        System.out.println("Welcome to Mafia");
	    System.out.println("Enter the number of players: ");
	    int playersCount=in.nextInt();
	    while(playersCount<=5){
	        System.out.println("Please choose numbers of player greater than 5");
	        playersCount=in.nextInt();
        }
        System.out.println("Choose a Character");
        System.out.println("1) Mafia");
        System.out.println("2) Detective");
        System.out.println("3) Healer");
        System.out.println("4) Commoner");
        System.out.println("5) Assign Randomly");
        int choice=in.nextInt();
        if(choice==5){
            choice=rand.nextInt(4)+1;
        }

        if(choice==1){

            userObject=new User<>(new Mafia());
            players=userObject.initiate(playersCount);
            Collections.sort(players,Player.ordering);
            System.out.println("You are "+userObject.getUser().getPlayerNumber());
            System.out.println("You are a Mafia. ");
            System.out.print("Other Mafias are - ");
            for(Player i:players){
                if(i.getType().equals("Mafia") && !i.equals(userObject.getUser())){
                    System.out.print("["+i.getPlayerNumber()+"]");
                }
            }
            System.out.println();
            userObject.getUser().playGame(players);

        }
        else if(choice==2){
            userObject=new User<>(new Detective());
            players=userObject.initiate(playersCount);
            Collections.sort(players,Player.ordering);
            System.out.println("You are "+userObject.getUser().getPlayerNumber());
            System.out.println("You are a Detective. ");
            System.out.print("Other Detectives are - ");
            for(Player i:players){

                if(i.getType().equals("Detective") && !i.equals(userObject.getUser())){
                    System.out.print("["+i.getPlayerNumber()+"]");
                }
            }
            System.out.println();
            userObject.getUser().playGame(players);

        }
        else if(choice==3){
            userObject=new User<>(new Healer());
            players=userObject.initiate(playersCount);
            Collections.sort(players,Player.ordering);
            System.out.println("You are "+userObject.getUser().getPlayerNumber());
            System.out.println("You are a Healer. ");
            System.out.print("Other Healers are - ");
            for(Player i:players){
                if(i.getType().equals("Healer") && !i.equals(userObject.getUser())){
                    System.out.print("["+i.getPlayerNumber()+"]");
                }
            }
            System.out.println();
            userObject.getUser().playGame(players);

        }
        else if(choice==4){
            userObject=new User<>(new Commoner());
            players=userObject.initiate(playersCount);
            Collections.sort(players,Player.ordering);
            System.out.println("You are "+userObject.getUser().getPlayerNumber());
            System.out.println("You are a Commoner. ");
            userObject.getUser().playGame(players);
        }
        else{
            System.exit(0);
        }
    }
}
