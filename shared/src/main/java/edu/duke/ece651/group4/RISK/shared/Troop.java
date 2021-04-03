package edu.duke.ece651.group4.RISK.shared;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class Troop implements Serializable {

    private final ArrayList<Unit> population;
    private final HashMap<String,Integer> dict;

    private Player owner;

    public Troop(int number, Player owner, Random rand) {
        this.owner = owner;
        this.population = new ArrayList<>();
        this.owner=new TextPlayer(owner.getName());
        this.dict=new HashMap<>();
        dict.put("Soldier LV0",number);
        for (int i = 0; i < number; i++) {
            Soldier s=new Soldier(rand);
            population.add(s);
        }
    }

    public Troop(int number, Player owner) {
        this.owner = owner;
        this.population = new ArrayList<>();
        this.owner=new TextPlayer(owner.getName());
        this.dict=new HashMap<>();
        dict.put("Soldier LV0",number);

        for (int i = 0; i < number; i++) {
            Soldier s=new Soldier(new Random());
            population.add(s);
        }
    }

    public Troop(ArrayList<Unit> subTroop, Player owner){
        this.population = subTroop;
        this.owner = new TextPlayer(owner.getName());
        this.dict=new HashMap<>();

        for(Unit u: subTroop){
            String name=u.getJobName();
            if(dict.get(name)==null){
                dict.put(name,1);
            }else{
                dict.put(name,dict.get(name)+1);
            }
        }
    }

    public Troop(ArrayList<Unit> subTroop,HashMap<String,Integer> myDict, Player owner){
        this.population = subTroop;
        this.owner = new TextPlayer(owner.getName());
        this.dict=myDict;
    }

    public Troop(HashMap<String,Integer> myDict, Player owner){
        this.population = null;
        this.owner = new TextPlayer(owner.getName());
        this.dict=myDict;
    }

    /**
     * Do battle between two troops
     * @param enemy shows the enemy troop attack in
     */
    public Troop combat(Troop enemy) {
        boolean attack=true;
        while (this.checkTroopSize() != 0 && enemy.checkTroopSize() != 0) {


            Unit myUnit =attack?this.getWeakest():this.getStrongest();
            Unit enemyUnit =attack?enemy.getStrongest():this.getWeakest();

            if (myUnit.fight(enemyUnit)) {
                enemy.loseUnit(enemyUnit);
            }else {
                this.loseUnit(myUnit);
            }
        }
        return enemy;
    }
    /**
     * Check population of troop
     */
    public int checkTroopSize() {
        return this.population.size();
    }

    /**
     * delete a specific unit from troop
     */
    private void loseUnit(Unit loss) {
        String name=loss.getJobName();
        this.dict.put(name,this.dict.get(name)-1);
        this.population.remove(loss);
    }

    /**
     * send a unit from troop
     */
    public Unit dispatchUnit() {
        return this.population.get(0);
    }

    /**
     * send a unit from troop
     */
    public Unit dispatchCertainUnit(String name) {
        if(this.dict.get(name)==null){
            throw new IllegalArgumentException("No enough Unit at this level");
        }
        for(Unit u:this.population){
            if(u.getJobName()==name){
                this.dict.put(name,this.dict.get(name)-1);
                Unit target=u;
                this.population.remove(u);
                return target;
            }
        }

        return null;
    }

    /**
     * receive a specific unit
     */
    public void receiveUnit(Unit target) {
        String name= target.getJobName();
        if(this.dict.get(name)==null){
            this.dict.put(name,1);
        }else{
            this.dict.put(name,this.dict.get(name)+1);
        }

        this.population.add(target);
    }


    public Unit getUnit(String name) {
        for(Unit u:this.population){
            if(u.getJobName()==name){
                return u;
            }
        }

        return null;
    }
    /**
     * Send out specific number of troops
     */
    public Troop sendTroop(int number){
        ArrayList<Unit> sub = new ArrayList<>();
        for(int i = 0; i < number; i++){
            Unit movedUnit = this.dispatchUnit();
            sub.add(movedUnit);
            this.loseUnit(movedUnit);
        }
        return new Troop(sub, getOwner());
    }

    public Troop sendTroop(Troop target){
        ArrayList<Unit> sub = new ArrayList<>();
        HashMap<String,Integer> subDict=target.getDict();

        if(!this.checkSend(target)){
            throw new IllegalArgumentException("Target sending troop has invalid size");
        }

        for(String s:subDict.keySet()){
            int num=subDict.get(s);
            for(int i=0;i<num;i++){
                sub.add(this.dispatchCertainUnit(s));
            }
        }


        return new Troop(sub, subDict,this.owner);
    }

    /**
     * Check if troop win the fight
     */
    public boolean checkWin(){
        return this.checkTroopSize()>0;
    }

    /**
     * receive specific number of troops
     */
    public void receiveTroop(Troop subTroop){
        while(subTroop.checkTroopSize() != 0){
            Unit newMember=subTroop.dispatchUnit();
            this.receiveUnit(newMember);
            subTroop.loseUnit(newMember);
        }
    }

    /**
     * return owner of troop
     */
    public Player getOwner(){

        return this.owner;
    }

    public Troop clone(){
        ArrayList<Unit> cloneList = new ArrayList<Unit>(population.size());
        for (Unit item : population) cloneList.add(item.clone());
        Troop clone=new Troop(cloneList,new TextPlayer(new String(this.owner.getName())));

        return clone;
    }

    public int checkUnitNum(String name){
        if(this.dict.get(name)==null) return 0;
        return this.dict.get(name);
    }

    public int updateUnit(String from, int levelUp,int num,int resource){
        if(this.checkUnitNum(from)<num){
            throw new IllegalArgumentException("No enough Unit to upgrade");
        }

        try{
            while(num>0){

                Soldier target=(Soldier)this.getUnit(from);

                resource=target.upGrade(target.getLevel()+levelUp,resource);

                this.dict.put(from,this.dict.get(from)-1);

                int newNum=this.dict.get(target.getJobName())==null?1:this.dict.get(target.getJobName())+1;
                this.dict.put(target.getJobName(),newNum);
                num--;
            }

        }catch(Exception e){

            throw new IllegalArgumentException(e.getMessage()+"\n"+num+" units not upgraded");
        }

        return resource;
    }

    public HashMap<String,Integer> getDict(){
        return this.dict;
    }

    public boolean checkSend(Troop target){
        HashMap<String,Integer> check=target.getDict();
        for( String s:check.keySet() ){
            if(this.dict.get(s)==null||this.dict.get(s)<check.get(s)) return false;
        }
        return true;
    }

    private Unit getStrongest(){
        int maxLevel=-1;
        Unit target=null;


        for(String s:this.dict.keySet()){

            if(dict.get(s)!=0){
                Soldier check=(Soldier)this.getUnit(s);
                if(maxLevel<check.getLevel()) {
                    maxLevel = check.getLevel();
                    target = check;
                }
            }
        }
        return target;
    }

    private Unit getWeakest(){

        Unit target=null;
        int minLevel=Integer.MAX_VALUE;
        for(String s:this.dict.keySet()){

            if(dict.get(s)!=0){
                Soldier check=(Soldier)this.getUnit(s);
                if(minLevel>check.getLevel()) {
                    minLevel= check.getLevel();
                    target = check;
                }
            }
        }
        return target;
    }

}

