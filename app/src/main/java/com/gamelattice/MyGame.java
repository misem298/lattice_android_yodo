/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gamelattice;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
/**
 *
 * @author Administrator
 */
public class MyGame {
    protected ArrayList<Checker> aicheckers;
    protected ArrayList<Checker> mycheckers;
    //protected ArrayList<Checker> mycheckersUnderAttack = new ArrayList<>();
    protected ArrayList myMoves;
    protected HashMap<Checker,ArrayList<Vector3f>> canEat = new HashMap<>();
    protected HashMap<Checker,ArrayList<Vector3f>> canMove = new HashMap<>();
    protected HashMap<Checker,ArrayList<Vector3f>> dangerMoves = new HashMap<>();
    protected HashMap<Checker,ArrayList<Vector3f>> dangerEat = new HashMap<>();
    protected ArrayList<Integer> placesOfBench = new ArrayList<>();
    protected ArrayList<Vector3f> freeJoints = new ArrayList();
    //protected int dimension;
    protected int dimensionX, dimensionY, dimensionZ;
    protected int blink, burst, smartLevel;
    protected boolean movesAre, moving, eating, moveating, flame, eated, premoving, birthing, shivering;
    protected boolean soundOn, damkaAllowed, showHints;
    protected boolean moveDid, passMove;
    protected boolean haveEat, haveMove;
    protected boolean checkerChecked, moveChecked, returnChecker, pickReturnJoint;
    protected boolean moveStored;
    protected boolean auto, spark;
    protected double birthTime, preMoveTime, swellTime;
    protected Checker checkerToMove,  checkerToBeEated, checkerToBirth;
    protected Vector3f[] eatVector;
    protected Vector3f moveVector, debrisVector, flameVector;
    protected String moveFrom, moveTo, moveKind;
    protected String gamer, nick;
    final int topbot;
    protected ArrayList<Vector3f> directions = new ArrayList();
    protected ArrayList<Vector3f> directionsUp = new ArrayList();
    protected SoundAndroid soundEffects;
    //protected GameHistory gameHistory;
    protected Checkers checkers;
    protected AssetManager assetManager;
    protected boolean testing = false;
    protected float soundVolume;

    public MyGame(ArrayList<Checker> aicheckers, ArrayList<Checker> mycheckers,
            int dX, int dY, int dZ, int topbot, AssetManager  assetManager, SoundAndroid soundEffects,
             String gamer, Node guiNode, BitmapFont guiFont) {
        this.assetManager = assetManager;
        this.topbot = topbot;
        this.soundOn = true;
        this.soundVolume = 1f;
        this.blink = 4;
        this.burst = 4;
        this.spark = false;
        this.eated = false;
        this.showHints = true;
        this.aicheckers = aicheckers;
        this.mycheckers = mycheckers;
        this.moveFrom = "";
        this.moveTo = "";
        this.moveKind = "";
        //this.gameHistory = gameHistory;
        this.dimensionX = dX;
        this.dimensionY = dY;
        this.dimensionZ = dZ;
        this.moveVector = new Vector3f();
        this.debrisVector = new Vector3f();
        this.flameVector = new Vector3f(100, 100, 100);
        this.movesAre = true;
        this.haveEat = false;
        this.haveMove = false;
        this.checkerChecked = false;
        this.moveChecked = false;
        this.moveDid = false;
        this.passMove = false;
        this.moveStored = false;
        this.auto = false;
        this.moving = false;
        this.premoving = false;
        this.shivering = false;
        this.eating = false;
        this.moveating = false;
        this.birthing = false;
        this.flame = false;
        this.damkaAllowed = true;
        this.returnChecker = false;
        this.pickReturnJoint = false;
        this.soundEffects = soundEffects;
        this.gamer = gamer;
        if (gamer.equals("ai")) this.nick = "Rival has ";
        else this.nick = "You have ";
        for (int i = -dimensionZ - 5; i < aicheckers.size(); i++) this.placesOfBench.add(i);

        this.directions.add(new Vector3f(2, 0, 2));
        this.directions.add(new Vector3f(-2, 0,  2));
        this.directions.add(new Vector3f(0, 2, 2));
        this.directions.add(new Vector3f(0, -2, 2));
        this.directions.add(new Vector3f(2, 0, -2));
        this.directions.add(new Vector3f(-2, 0, -2));
        this.directions.add(new Vector3f(0, 2, -2));
        this.directions.add(new Vector3f(0, -2, -2));
         this.directionsUp.add(new Vector3f(2, 0, 2));
        this.directionsUp.add(new Vector3f(-2, 0,  2));
        this.directionsUp.add(new Vector3f(0, 2, 2));
        this.directionsUp.add(new Vector3f(0, -2, 2));
        makeBench();
    }

    protected void autoGame(){
        if (areCheckersInField(aicheckers) == 0) return;
        if (!(haveEat | haveMove)) if (!selectMeEat()) selectMeMove();
        movesAre = haveEat | haveMove;
            if (!movesAre & !pickReturnJoint) {
            GameStart.hod = aicheckers.get(0).defaultcolor;
            return;
        }
        if (haveEat) {  // if eat
            if (eated) {
                getEatVectors(checkerToMove);
                if (!checkerToMove.eatList.isEmpty()) {
                    if (auto) {
                        randomEat(checkerToMove.eatList);
                        makeEating();
                    }
                    else if(moveChecked) makeEating();
                    eated = false;
                    return;
                }
                eated = false;
            }
            else {
                makeEating();
                return;
            }
        }
        else {       // **** if easy move *****
            moveFrom =  convertToAa1(checkerToMove.v);
            if (moveVector.z == -checkerToMove.startZ & !checkerToMove.mamka) returnChecker = true;
            checkerToMove.setMove(moveVector, damkaAllowed);
            moveTo =  convertToAa1(moveVector);
            if (soundOn) GameStart.playSound(soundEffects.pick_audio, 0);
            moveKind = "move";
            //moving = true;
            moveating = true;
            //gameHistory.reccordHistory(gamer, mycheckers, moveFrom, moveTo, moveKind);
        }
        resetGame();
        getFreeJoints();
        if (returnChecker) {
            returnCheckerToPlay();
            return;
        }
        GameStart.hod = aicheckers.get(0).defaultcolor;
    }

    protected void goMyGame(Joint pickedJoint, Checker pickedChecker){
        //movesAre = haveEat | haveMove;
        if (!(haveEat | haveMove)) if (!selectMeEat()) selectMeMove();
        //haveEat = !canEat.isEmpty();
        if (haveEat & !moveChecked) setPulseEffects();
        //haveMove = !canMove.isEmpty();
        movesAre = haveEat | haveMove;
        if (!movesAre & !pickReturnJoint)  { // pass move to ai if no moves
            GameStart.hod = aicheckers.get(0).defaultcolor;
            //gameHistory.reccordHistory(gamer, mycheckers, moveFrom, moveTo);
            return;
        }
        if ((moveChecked & checkerChecked) | eated) doMyMove(pickedJoint, pickedChecker);
        if (checkerChecked)  {
            pickedChecker.setBright(3);
            hideAttackedCheckers();
        }
        if (showHints & !checkerChecked & !haveEat) showAttackedCheckers();

    }

    public void doMyMove(Joint pickedJoint, Checker pickedChecker) {
        if (testing) {
            checkerToMove = pickedChecker;
            moveVector = pickedJoint.v;
            if (moveVector.z == -checkerToMove.startZ & !checkerToMove.mamka) returnChecker = true;
            checkerToMove.setMove(moveVector, damkaAllowed);
            moveating = true;
            resetGame();
            //autoGame();
         return;
        }
        if (areNoCheckers(pickedJoint.v) | eated) {
                if ((pickedJoint.z - pickedChecker.z) * topbot == 2  &
                    ((Math.abs(pickedChecker.x - pickedJoint.x) == 0 &
                    Math.abs(pickedChecker.y - pickedJoint.y) == 2) ^
                    (Math.abs(pickedChecker.x - pickedJoint.x) == 2 &
                    Math.abs(pickedChecker.y - pickedJoint.y) == 0))) {
                    if (!haveEat) { //prostoi hod
                        //moving(pickedJoint, pickedChecker);
                        checkerToMove = pickedChecker;
                        moveVector = pickedJoint.v;
                        autoGame();
                        checkerChecked = false;
                    }
                }
                if ((Math.abs(pickedChecker.z - pickedJoint.z) == 4 & // hod s edoj
                    (Math.abs(pickedChecker.y - pickedJoint.y) == 0 &
                     Math.abs(pickedChecker.x - pickedJoint.x) == 4) ^
                    (Math.abs(pickedChecker.y - pickedJoint.y) == 4 &
                     Math.abs(pickedChecker.x - pickedJoint.x) == 0)) | eated) {
                    checkerToBeEated = getCheckerByXYZ(new Vector3f(
                        pickedChecker.x + (pickedJoint.x - pickedChecker.x)/2,
                        pickedChecker.y + (pickedJoint.y - pickedChecker.y)/2,
                        pickedChecker.z + (pickedJoint.z - pickedChecker.z)/2), aicheckers);
                    if (checkerToBeEated != null | eated) {
                        //if (checkerToBeEated != null) eating = true;
                        //eated = false;
                        //eating(pickedJoint, pickedChecker);
                        checkerToMove = pickedChecker;
                        moveVector = pickedJoint.v;
                        autoGame();
                        if (!haveEat) resetPulseEffects();
                        //pickedJoint = null;
                    }
                }
        }
    }


    private void makeEating() {
        eating = true;
        moveating = true;
        if (soundOn) soundEffects.soundPool.play(soundEffects.pick_audio,soundVolume,soundVolume,0,0,1);
        debrisVector.set(checkerToBeEated.v);
        setCheckersToBench();
        moveFrom =  convertToAa1(checkerToMove.v);
        moveTo =  convertToAa1(moveVector);
        moveKind = "take";
        if (moveVector.z == -checkerToMove.startZ & !checkerToMove.mamka) returnChecker = true;
        checkerToMove.setMove(moveVector, damkaAllowed);// set move vector
    }

    protected boolean selectMeMove(){
        for (Checker c: mycheckers) {
            if (!c.moveList.isEmpty()) canMove.put(c, c.moveList);
        }
       if(!canMove.isEmpty()) {
           //if (smartLevel > 0) canMove = thinkMoreAboutMove(canMove);
           if (smartLevel > 0) thinkMoreAboutM();
           checkerToMove = canMove.keySet().toArray(new Checker[canMove.size()-1])[(int)((Math.random() - 0.01f) * canMove.size())];
           randomMove(canMove.get(checkerToMove));
           haveMove = true;
           preMoveTime = System.currentTimeMillis();
           premoving = true;
           return true;
       }
       return false;
    }

    protected boolean selectMeEat(){
        for (Checker c: mycheckers) {
            getEatVectors(c);
             if (!c.eatList.isEmpty()) canEat.put(c, c.eatList); //map of checkers & possible moves
        }
        //System.out.println("selectMeEat" + canEat.values() );
       if(!canEat.isEmpty()) {
           if (smartLevel > 0) thinkMoreAboutEat();
           checkerToMove = canEat.keySet().toArray(new Checker[canEat.size()])[(int)((Math.random() - 0.01f) * canEat.size())];
           //checkerToEat = selectRandomChecker(canEat.keySet());
           randomEat(canEat.get(checkerToMove));
           haveEat = true;
           preMoveTime = System.currentTimeMillis();
           premoving = true;
           return true;
       }
       return false;
    }


    private void getEatVectors(Checker c){
        //ArrayList<Vector3f> vv = new ArrayList<>();
        c.eatList.clear();
        c.moveList.clear();
        for (Vector3f d: directions) {
            Vector3f food = c.v.add(d);
            while (isJointIn(food)) {
                if (!areNoCheckers(food)){ // if are checkers in food point
                    for (Checker me: mycheckers) {
                        if (isChecker(me, food)) {
                            food = food.set(100,100,100);
                            break;
                        }
                    }
                    for (Checker ai: aicheckers) {
                        //System.out.println("xx " + ai.v + " food " + food + " " + food.add(v));
                        if (isChecker(ai, food)) {
                            addEatList(c, ai, food, d);
                            food = food.set(100,100,100);
                            break;
                        }
                    }
                    //food.set(100,100,100);
                    //break;
                }
                else {
                    if (!c.damka) {
                            if(d.z * topbot == 2) c.moveList.add(new Vector3f(food));
                            food.set(100,100,100);
                            break;
                    }
                    c.moveList.add(new Vector3f(food));
                }
                food = food.add(d); // look next joint
            }
        }
    }


    private void addEatList(Checker c, Checker ai, Vector3f move, Vector3f v) {
        if (areNoCheckers(move.add(v))) {
            ai.mayBeEated = true;
            c.eatList.add(new Vector3f(ai.v));
            c.eatList.add(new Vector3f(move.add(v)));
            if (c.damka) addEatList(c, ai, move.add(v), v);
        }
    }

    protected Checker selectRandomChecker(Set<Checker> checkersSet){
        // select the number of random checker exluding damkas
        int key = (int)((Math.random() - 0.01f) *
                  (checkersSet.size() - getNumberOfDamka(checkersSet)));
        int i = 0;
        for (Checker c : checkersSet) {
            if(!c.damka) {
                if (i == key) { // return random checker
                    moveFrom = convertToAa1(c.v);
                    return c;
                }
                i++;
            }
        }
        return null;
    }

    private int getNumberOfDamka(Set<Checker> checkersSet) {
        int i = 0;
        for (Checker c : checkersSet) {
            if(!c.damka) i++;
        }
        return i;
    }

    protected void randomMove(ArrayList<Vector3f> moves) { // select the random direction of move
        moveVector = moves.get((int)((Math.random() - 0.01f) * moves.size())); // random move vector
    }

    protected void randomEat(ArrayList<Vector3f> moves) { // select random checker which should be eated
        int key = (int)(Math.random() * moves.size() - 0.02f) / 2; // low random position
        //eatVector[1] = moves.get(2* key + 1); //  step position
        moveVector = moves.get(2 * key + 1);
        //debrisVector = moves.get(2 * key); //  eated position
        checkerToBeEated = getCheckerByXYZ(moves.get(2 * key), aicheckers);
        moveTo = convertToAa1(moveVector);// convertToAa1(eatVector[1]);
    }

    private void setCheckersToBench(){// set remove vector , not yet moving
        ArrayList<Integer> places = new ArrayList();
        places.addAll(placesOfBench);
        for (Checker c: aicheckers) {
            if (!isJointIn(c.v)) places.remove((Integer)Math.round(c.v.z)) ;
        }
        //System.out.println("setCheckersToBench" + places.toString()+" " + places.size() + "\n" + placesOfBench.toString());
        checkerToBeEated.setMove(new Vector3f(topbot * (dimensionX + 9),
                (dimensionY + 10), places.get(0)), true);
    }

    // smart moves
    private void thinkMoreAboutM(){
        switch(smartLevel) {
            case 1:
                selectSafeMoves();
                break;
            case 2:
                selectSafeMoves();
                selectAttackedCheckers();
                break;
            case 4:
                selectAttackedCheckers();
                selectNotAvengedCheckers();
                selectSafeMoves();
                break;
            case 8:
                selectAttackedCheckers();
                selectNotAvengedCheckers();
                selectSafeMoves();
                removePodstavaCheckers();
                selectMovesToDamka();
                break;
            case 16:
                //System.out.println("toDamka " + canMove);
                selectAttackedCheckers();
                //System.out.println("attacked " + canMove);
                selectNotAvengedCheckers();
                //System.out.println("avenged " + canMove);
                selectSafeMoves();
                //System.out.println("safeMoves " + canMove);
                removePodstavaCheckers();
                //System.out.println("Podstava " + canMove);
                selectMovesToDamka();
                //System.out.println("toDamka " + canMove);
                break;
            case 32:
                selectMovesToDamka();
               // System.out.println("toDamka " + canMove);
                selectAttackedCheckers();
                //System.out.println("attacked " + canMove);
                selectNotAvengedCheckers();
                //System.out.println("avenged " + canMove);
                selectSafeMoves();
                //System.out.println("safe " + canMove);
                selectMovesToCoverMap();
                //System.out.println("safeMoves " + canMove);
                removePodstavaCheckers();
                //System.out.println("Podstava " + canMove);
                selectMovesWithNextMoves();
                //System.out.println("WithNextMoves " + canMove);
                selectAttackingMoves();
                //System.out.println("AttackingMoves " + canMove);
                selectCheckersHasWayToDamkaMap();
               // System.out.println("HasWayToDamka " + canMove);
        }
    }

    private void selectMovesWithNextMoves() {
        HashMap<Checker,ArrayList<Vector3f>> mapWithMovesAfterMoves = new HashMap();
        mapWithMovesAfterMoves.putAll(getMapWithFutureMoves(canMove));
        if (!mapWithMovesAfterMoves.isEmpty()) {
            canMove.clear();
            canMove.putAll(mapWithMovesAfterMoves);
        }

}

    private void selectMovesToDamka(){
        HashMap<Checker,ArrayList<Vector3f>> movesToDamkaMap = new HashMap();
        movesToDamkaMap.putAll(getMovesToDamkaMap(canMove));
        if(!movesToDamkaMap.isEmpty()) {
            canMove.clear();
            canMove.putAll(movesToDamkaMap);
        }
    }

    private void removePodstavaCheckers(){
        ArrayList<Checker> mycheckersPodstava = new ArrayList<>();
        for (Checker me: canMove.keySet()) {
            for (Checker other: mycheckers ) {
                if (isDangerAroundD(me, other.v, aicheckers )) {
                    mycheckersPodstava.add(me) ;
                    break;
                }
            }
        }
        if (mycheckersPodstava.size() < canMove.size()) {
            canMove.keySet().removeAll(mycheckersPodstava);
            //System.out.println(mycheckersNotUnderAttack + " checkExistingDanger " + mycheckersNotUnderAttack.get(0).v);
        }
    }

    private void selectNotAvengedCheckers(){
            ArrayList<Checker> avendedCheckers = new ArrayList();
            avendedCheckers.addAll(getCheckersCanBeAvenged(canMove));
            if (avendedCheckers.size() < canMove.size()) canMove.keySet().removeAll(avendedCheckers);
    }

    private void selectAttackedCheckers() {
        ArrayList<Checker> attackedCheckers = new ArrayList();
        attackedCheckers.addAll(getAttackedCheckers(canMove));
        if (!attackedCheckers.isEmpty()) canMove.keySet().retainAll(attackedCheckers);
    }

    private void selectSafeMoves() {
        HashMap<Checker,ArrayList<Vector3f>> safeMovesMap = new HashMap<>();
        for (Entry<Checker,ArrayList<Vector3f>> entry: canMove.entrySet()) {
            ArrayList<Vector3f> safeMoves = new ArrayList<>();
            for (Vector3f move: entry.getValue()) {
                if (!isDangerAroundD(entry.getKey(), move, aicheckers)) {
                    safeMoves.add(move);
                    //System.out.println(" AroundAfterMove  checker " + entry.getKey().v + " d move   " + v);
                }
            }
            if (!safeMoves.isEmpty()) safeMovesMap.put(entry.getKey(), safeMoves);
        }
        if (!safeMovesMap.isEmpty()) {
            canMove.clear();
            canMove.putAll(safeMovesMap);
        }
    }

    private void selectAttackingMoves() {
        HashMap<Checker,ArrayList<Vector3f>> attackingMovesMap = new HashMap<>();
        for (Entry<Checker,ArrayList<Vector3f>> entry: canMove.entrySet()) {
            ArrayList<Vector3f> attackingMoves = new ArrayList<>();
            for (Vector3f move: entry.getValue()) {
                if (isEatFromHere(entry.getKey(), move, entry.getKey().v.subtract(move))) attackingMoves.add(move);
            }
            if (!attackingMoves.isEmpty()) attackingMovesMap.put(entry.getKey(), attackingMoves);
        }
        if (!attackingMovesMap.isEmpty()) {
            canMove.clear();
            canMove.putAll(attackingMovesMap);
        }
    }


    private void selectCheckersHasWayToDamkaMap(){
        HashMap<Checker,ArrayList<Vector3f>> waysToDamkaMap = new HashMap<>();
        for (Entry<Checker,ArrayList<Vector3f>> entry: canMove.entrySet()) {
            ArrayList<Vector3f> movesToEnd = new ArrayList<>();
            if (!entry.getKey().mamka) {
                for (Vector3f v: entry.getValue()) {
                    if (v.z == (2 * dimensionZ - 1) * topbot) break;
                    if (isWayToDamkaSafe(v)) movesToEnd.add(v);
                }
            }
            if (!movesToEnd.isEmpty()) waysToDamkaMap.put(entry.getKey(), movesToEnd);
        }
        if (!waysToDamkaMap.isEmpty()) {
            canMove.clear();
            canMove.putAll(waysToDamkaMap);
        }
    }

    private boolean isWayToDamkaSafe(Vector3f v) {
        boolean safeStep = true;
        Vector3f w = new Vector3f(v);
        for (Vector3f up: directionsUp) {
            w.set(v.add(up.mult(topbot)));
            if (!isJointIn(w)) {
                safeStep = false;
                break;
            }
            if (w.z  == (2 * dimensionZ - 1) * topbot) return areNoCheckers(w);
            for (Vector3f d: directions) {
                safeStep = true;
                for (Checker ai: aicheckers) {
                        if (isChecker(ai, new Vector3f(new Vector3f(w.add(d)))) &
                                isJointFree(new Vector3f(w.add(d.mult(-1))))) {
                            safeStep = false;
                            break;
                        }
                }
                if (safeStep) break ; //isWayToDamkaSafe(w);
            }
            if (safeStep) break;//safeWay = safeStep & safeStep;//System.out.println("isWayToDamkaSave " + w);
        }
        if (safeStep) safeStep = isWayToDamkaSafe(w);
        return safeStep;//isWayToDamkaSafe(w);
    }

    private void selectMovesToCoverMap(){
        ArrayList<Vector3f> coverMoves = new ArrayList<>();
        HashMap <Checker,ArrayList<Vector3f>> coverMap = new HashMap<>();
        for (Checker c : getAttackedCheckers(canMove)){
            for (Vector3f d: getAttackDirections(c.v, aicheckers)) {
                for (Entry<Checker,ArrayList<Vector3f>> entry: canMove.entrySet()) {
                    for (Vector3f w: entry.getValue()) {
                        if (w.equals(c.v.add(d.mult(-1)))) coverMoves.add(w);
                    }
                if (!coverMoves.isEmpty()) coverMap.put(entry.getKey(), coverMoves);
                }
            }
        }
        if (!coverMap.isEmpty()) {
            canMove.clear();
            canMove.putAll(coverMap);
        }
    }


    private ArrayList<Checker> getAttackedCheckers(HashMap<Checker,ArrayList<Vector3f>> movesMap) {
        ArrayList<Checker> mycheckersUnderAttack = new ArrayList<>();
        for (Checker me: movesMap.keySet()) {
            if (isDangerAroundD(me, me.v, aicheckers)) mycheckersUnderAttack.add(me) ;
        }
        return  mycheckersUnderAttack;
    }

    private ArrayList getCheckersCanBeAvenged(HashMap<Checker,ArrayList<Vector3f>> movesMap){
        ArrayList<Checker> mycheckersRevanged = new ArrayList<>();
        for (Checker me: movesMap.keySet()) {
            for (Vector3f d: getAttackDirections(me.v, aicheckers)) {
                if (isDangerAroundD(me, me.v.add(d), mycheckers)) {
                    mycheckersRevanged.add(me) ;
                    break;
                }
            }
        }
        return mycheckersRevanged;
    }

    private ArrayList<Vector3f> getAttackDirections(Vector3f point, ArrayList<Checker> enemies){
        ArrayList<Vector3f> attackDirections = new ArrayList();
        for (Checker en: enemies) {
            for (Vector3f d: directions) {
                if (isChecker(en, new Vector3f(point.add(d)))) {
                   if (areNoCheckers(new Vector3f(point.add(d.mult(-1))))) {
                       attackDirections.add(d.mult(-1));
                   }
                }
            }
        }
        return attackDirections;
    }

    private HashMap<Checker,ArrayList<Vector3f>> getMapWithFutureMoves(HashMap<Checker,ArrayList<Vector3f>> movesMap){
        HashMap<Checker,ArrayList<Vector3f>> newMap = new HashMap<>();
        for (Entry<Checker,ArrayList<Vector3f>> entry: movesMap.entrySet()) {
            if (!entry.getKey().mamka) {
                for (Vector3f move: entry.getValue()) {
                    for(Vector3f d: directions) {
                        if (d.z * (move.z-entry.getKey().z) > 0) { // if forward move
                            if (areNoCheckers(move.add(d))) newMap.put(entry.getKey(), entry.getValue());
                            break;
                        }
                    }
                }
            }
        }
        return newMap;
    }

    private HashMap<Checker,ArrayList<Vector3f>> getMovesToDamkaMap(HashMap<Checker,ArrayList<Vector3f>> movesMap){
        HashMap<Checker,ArrayList<Vector3f>> movesToDamkaMap = new HashMap<>();
        for (Entry<Checker,ArrayList<Vector3f>> entry: movesMap.entrySet()) {
            ArrayList<Vector3f> movesToDamka = new ArrayList<>();
            if (!entry.getKey().mamka) {
                for (Vector3f move: entry.getValue()) {
                        if ((move.z + entry.getKey().startZ) == 0) {
                            movesToDamka.add(move);
                        }
                    //System.out.println("checkWaysToDamka " + entry.getKey()+ " " + entry.getValue() + "ToDamka" + movesToDamka );
                    if (!movesToDamka.isEmpty()) movesToDamkaMap.put(entry.getKey(),entry.getValue());
                }
            }
        }
        return movesToDamkaMap;
    }

    private boolean isDangerAroundD(Checker ex, Vector3f place, ArrayList<Checker> checkers) {
        //checks if ai checker in "place" may be eated, ex - my checker eated before
        // or if my checker in "place" me be eated by enemies
        for (Vector3f d: directions) {
            if (areNoCheckers(place.add(d)) | isChecker(ex,place.add(d))) { //if in +d no checkers exluding me
                int k = 0 ;
                while (isJointIn(place.add(d.mult(-k)))) {
                    k++;
                    //System.out.println("isDangerAroundD " + k + " move.add(d) " + move.add(d) + gamer);
                    for (Checker c: checkers) {
                        if (isChecker(c, place.add(d.mult(-k)))) {
                            if (k == 1 | c.damka) {
                                //System.out.println("isDangerAroundD " + k + " attacking " + c.v);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean areNoCheckersExcludingMe(Checker me, Vector3f v) {
        // checks if are others checkers besides "me" in point v
        if (isJointIn(v)){
            ArrayList<Checker> allCheckers = new ArrayList<>();
            allCheckers.addAll(aicheckers);
            allCheckers.addAll(mycheckers);
            for (Checker c: allCheckers) {
                if (c.v.equals(v) & !c.v.equals(me.v)) return false;
            }
            return true;
        }
        return false;
    }

// smart eat
    private void thinkMoreAboutEat(){
       switch(smartLevel) {
               case 1:
                   selectEatMapExistingDanger();
                   break;
               case 2: case 4: case 8: case 16:
                   selectEatMapExistingDanger();
                   selectSafeEat();
               case 32:
                   //System.out.println("AboutEat b" + canEat);
                   selectEatMapExistingDanger();
                   //System.out.println("AboutEat e" + canEat);
                   selectSafeEat();
                   //System.out.println("AboutEat s" + canEat);
                   selectEatWithNextEat();
                   //System.out.println("AboutEat n" + canEat);
                   break;
       }
    }

    private void selectSafeEat() {
        HashMap<Checker,ArrayList<Vector3f>> safeEatsMap = new HashMap<>();
        for (Entry<Checker,ArrayList<Vector3f>> entry: canEat.entrySet()) {
            ArrayList<Vector3f> safeEats = new ArrayList<>();
            for (int i = 0; i < entry.getValue().size(); i = i + 2) {
                    if (!isDangerAroundD(getCheckerByXYZ(entry.getValue().get(i), aicheckers),
                                         entry.getValue().get(i + 1), aicheckers)) {
                        safeEats.add(entry.getValue().get(i));
                        safeEats.add(entry.getValue().get(i + 1));
                        //System.out.println(" checker  " + entry.getKey().v + " d move   " + v);
                    }
            }
            if (!safeEats.isEmpty()) safeEatsMap.put(entry.getKey(), safeEats);
        }
        if (!safeEatsMap.isEmpty())  {
            canEat.clear();
            canEat.putAll(safeEatsMap);
        }
    }

    private void selectEatMapExistingDanger() { // smartlevel 1 (2^0)
        ArrayList<Checker> mycheckersNotUnderAttack = new ArrayList<>();
        for (Checker me: canEat.keySet()) {
            if (!isDangerAroundD(me, me.v, aicheckers)) mycheckersNotUnderAttack.add(me) ;
        }
        if (mycheckersNotUnderAttack.size() < canEat.size())
           canEat.keySet().removeAll(mycheckersNotUnderAttack);
    }

    private void selectEatWithNextEat(){
        HashMap<Checker,ArrayList<Vector3f>> eatWithNextEatMap = new HashMap();
        for (Entry<Checker,ArrayList<Vector3f>> entry: canEat.entrySet()) {
            ArrayList<Vector3f> secondaryEatList = new ArrayList<>();
            for (int i = 0; i < entry.getValue().size(); i = i + 2) { // vectors after eat (each second in list)
                if (isEatFromHere(entry.getKey(), entry.getValue().get(i + 1),  // check Eat from landing point
                                  entry.getValue().get(i + 1).subtract(entry.getValue().get(i)))) {
                secondaryEatList.add(entry.getValue().get(i));
                secondaryEatList.add(entry.getValue().get(i + 1));
                }
            }
            if (!secondaryEatList.isEmpty()) eatWithNextEatMap.put(entry.getKey(), secondaryEatList);
        }
        if (!eatWithNextEatMap.isEmpty()) {
            canEat.clear();
            canEat.putAll(eatWithNextEatMap);
        }
    }

    //manual game

    private int checkLine(Vector3f jointVect, Vector3f checkerVect) {
        int nAi = 0;
        boolean nextAi = false;
        Vector3f sign = new Vector3f(getMoveDirection(checkerVect, jointVect));
           for (int i = 1; i < Math.abs(jointVect.z - checkerVect.z)/2; i++) {
            Vector3f lineV = new Vector3f(checkerVect.x + 2 * i * sign.x, checkerVect.y + 2 * i * sign.y, checkerVect.z + 2 * i * sign.z);
            for (Checker c: mycheckers) { //check if in line are my checkers
                if (c.v.equals(lineV)) return -1;
            }
            for (Checker c: aicheckers) { //check if in line are ai checkers
                if (c.v.equals(lineV)) {
                    //eatVector[0].set(c.v);
                    checkerToBeEated = c;
                    if (nextAi) return -1;
                    nextAi = true;
                    nAi = nAi + 1;
                }
            }
            nextAi = false;
        }
        return nAi;
    }

    private Vector3f getMoveDirection(Vector3f checker, Vector3f joint){
        int signZ = Math.round((joint.z - checker.z) / Math.abs(joint.z - checker.z));
        int signX = 0;
        if (joint.x != checker.x) signX = Math.round((joint.x - checker.x) / Math.abs(joint.x - checker.x));
        int signY = 0;
        if (joint.y != checker.y) signY = Math.round((joint.y - checker.y) / Math.abs(joint.y - checker.y));
        return new Vector3f(signX, signY, signZ);
     }

    private int areCheckersInField(ArrayList<Checker> chks){
        int i = 0;
        for (Checker c: chks) if (Math.abs(c.z) < 2 * dimensionZ) i++;
        return i;
    }

    protected boolean isJointFree(Vector3f v){
        if (isJointIn(v)) {
            for (Checker c: mycheckers) if (c.v.equals(v)) return false;
            for (Checker c: aicheckers) if (c.v.equals(v)) return false;
            return true;
        }
        return false;
    }

    protected boolean isChecker(Checker checker, Vector3f v){
        if (isJointIn(v)) return checker.v.equals(v);
        return false;
    }

    protected boolean areNoCheckers(Vector3f v) {
        if (isJointIn(v)){
            ArrayList<Checker> allCheckers = new ArrayList<>();
            allCheckers.addAll(aicheckers);
            allCheckers.addAll(mycheckers);
            for (Checker c: allCheckers) {
                if (c.v.equals(v)) return false;
            }
            return true;
        }
        return false;
    }

    protected boolean areSpecCheckers(Vector3f v, ArrayList<Checker> specCheckers) {
        if (isJointIn(v)){
            for (Checker c: specCheckers) {
                if (c.v.equals(v)) return true;
            }
            return false;
        }
        return false;
    }

    protected boolean isJointIn(Vector3f v){
        return (Math.abs(v.x) < 2 * dimensionX & Math.abs(v.y) < 2 * dimensionY & Math.abs(v.z) < 2 * dimensionZ );
    }

    private boolean isEatFromHere(Checker ch, Vector3f start, Vector3f backDir){
        ArrayList<Vector3f> usedDirs = new ArrayList(directions);
        if (!backDir.equals(Vector3f.ZERO)) usedDirs.remove(backDir);
        for (Vector3f d: usedDirs) {
            if (isSpecCheckerFirstInLine(ch, start, d, aicheckers))
                return (areNoCheckers(start.add(d.mult(2)))) ;
        }
        return false;
    }

    private boolean isSpecCheckerFirstInLine(Checker ch, Vector3f start, Vector3f direction, ArrayList<Checker> specCheckers){
        Vector3f p = new Vector3f(start.add(direction));
        while (isJointIn(p)) {
            if (!areNoCheckers(p)) return areSpecCheckers(p, specCheckers);
            if (!ch.damka) return false;
            p.add(direction,p);
        }
    return false;
    }

     private Checker getCheckerFromBench() {
        for (Checker c: mycheckers) {
            if (!isJointIn(c.v)) return c;
        }
        return null;
     }
     protected void reccordMove(Vector3f moveFrom , Vector3f moveTo) {
        this.moveFrom = Character.toString((char)((moveFrom.x + 2 * dimensionX - 1) / 2 + 65)) +
                        Character.toString((char)((moveFrom.y + 2 * dimensionY - 1) / 2 + 97)) +
                        Character.toString((char)((moveFrom.z + 2 * dimensionZ - 1) / 2 + 49));
        this.moveTo = Character.toString((char)((moveTo.x + 2 * dimensionX - 1) / 2 + 65)) +
                      Character.toString((char)((moveTo.y + 2 * dimensionY - 1) / 2 + 97)) +
                      Character.toString((char)((moveTo.z + 2 * dimensionZ - 1) / 2 + 49));
        //moveDid = false;
    }

     protected String convertToAa1(Vector3f v){
        return Character.toString((char)((v.x + 2 * dimensionX - 1) / 2 + 65)) +
               Character.toString((char)((v.y + 2 * dimensionY - 1) / 2 + 97)) +
               Character.toString((char)((v.z + 2 * dimensionZ - 1) / 2 + 49));
    }

    protected void setPulseEffects() {
        if (soundOn) soundEffects.soundPool.play(soundEffects.heart_audio,soundVolume,soundVolume,0,0,1);
         if (checkerChecked) {
            for (Checker ai: aicheckers) ai.mayBeEated = false;
            getEatVectors(checkerToMove);
        }
        for (Checker ai: aicheckers) {
            if (ai.mayBeEated) {
                ai.setRandomBright();
                ai.pulseRadius(0.6f);
            }
        }
    }

    protected void resetPulseEffects() {
        for (Checker ai: aicheckers) {
            if (ai.mayBeEated) {
                ai.resetColor();
                ai.mayBeEated = false;
                ai.mesh.updateGeometry(10, 10, 0.5f);
            }
        }
    }

    private void showAttackedCheckers() {
        for (Checker c: mycheckers) {
            if (isDangerAroundD(c, c.v, aicheckers)) {
                c.setRandomBright();
                c.pulseRadius(0.6f);
                if (soundOn) soundEffects.soundPool.play(soundEffects.heart_audio,soundVolume,soundVolume,0,0,1);
            }
        }
    }

    private void hideAttackedCheckers() {
        for (Checker c: mycheckers) c.resetChecker();
    }

    protected void resetGame(){
        canEat.clear();
        canMove.clear();
        //if (checkerToEat != null)  checkerToEat.resetColor();
        if (checkerToMove != null) checkerToMove.resetColor();
        if (checkerToBeEated != null)  checkerToBeEated.resetColor();
        resetFlags();
    }

    protected void resetFlags() {
        movesAre = true;
        haveMove = false;
        haveEat = false;
        checkerChecked = false;
        moveChecked = false;
        //eating = false;
        //moveating = false;
        //blink = 0;
    }

    protected void returnCheckerToPlay(){
        checkerToBirth = getCheckerFromBench();
        if (checkerToBirth != null) {
            flameVector.set(new Vector3f(selectRandomReturnPoint()));
            if (auto) {
                //System.out.println("returnCheckerToPlay" + flameVector);
                if (flameVector.z != 100) {
                    flame = true;
                    birthTime = System.currentTimeMillis();
                    birthing = true;
                    //System.out.println("returnCheckerToPlay " + checkerToBirth.v + " hod " + Main.hod);
                }
            }
        else  if (!freeJoints.isEmpty()) pickReturnJoint = true;
        }
        returnChecker = false;
    }

    private void getFreeJoints() {
        freeJoints.clear();
        int z = mycheckers.get(0).startZ;
        for (int i = 0; i < 2 * dimensionY; i++) {
            int y = 2 * i - 2 * dimensionY + 1;
            for (int j = 0; j < 2 * dimensionX; j++) {
                int x = 2 * j - 2 * dimensionX + 1;
                Vector3f v = new Vector3f(x, y, z);
                if (z < 0) {
                    if ((i + j) % 2 == 0) if (areNoCheckers(v)) freeJoints.add(v);
                }
                if (z > 0) {
                    if ((i + j) % 2 != 0) if (areNoCheckers(v)) freeJoints.add(v);
                }
            }
        }
    }

    protected void returnPickedCheckerToPlay(Lattice lattice, Joint pickedJoint) {
        lattice.showFreeJoints(freeJoints);
        //    System.out.println(lattice.myJoints.contains(pickedJoint));
        //if (!lattice.myJoints.contains(pickedJoint)) return;
        if (pickedJoint != null && pickedJoint.z == mycheckers.get(0).startZ) {
            flameVector = new Vector3f(pickedJoint.v);
            flame = true;
            birthing = true;
            lattice.resetMyAiJoints();
            pickReturnJoint = false;
        }
        //System.out.println("returnPickedCheckerToPlay pickedJoint.v" + pickedJoint.v + " hod " + GameStart.hod  );
    }

    private Vector3f selectRandomReturnPoint() {
        /*freeJoints.clear();
        int z = mycheckers.get(0).startZ;
        for (int i = 0; i < 2 * dimensionY; i++) {
            int y = 2 * i - 2 * dimensionY + 1;
            for (int j = 0; j < 2 * dimensionX; j++) {
                int x = 2 * j - 2 * dimensionX + 1;
                Vector3f v = new Vector3f(x, y, z);
                if (z < 0) {
                    if ((i + j) % 2 == 0) if (areNoCheckers(v)) freeJoints.add(v);
                }
                if (z > 0) {
                    if ((i + j) % 2 != 0) if (areNoCheckers(v)) freeJoints.add(v);
                }
            }
        }*/
        //System.out.println("selectRandomReturnPoint " + freeJoints.size() + "  " + freeJoints);
        if (!freeJoints.isEmpty()) {
            if (smartLevel > 10) selectAttackingReturnJoints();
            return freeJoints.get((int)((Math.random() * freeJoints.size() - 0.01f)));
        }
        return new Vector3f(100,100,100);
    }

    private void selectAttackingReturnJoints() {
        ArrayList<Vector3f> attackingJoints = new ArrayList<>();
        for (Vector3f fj: freeJoints) {
            System.out.println(fj.z + " - " + fj.y);
            for (Vector3f d: directionsUp) {
                System.out.println(d + " direct" );
                for (Checker ai: aicheckers) {
                  if  (isChecker(ai, new Vector3f(new Vector3f(fj.add(d.mult(topbot))))) &
                                areNoCheckers(new Vector3f(fj.add(d.mult(2*topbot)))))
                      attackingJoints.add(fj);
                }
            }
        }
        if (!attackingJoints.isEmpty()) {
            freeJoints.clear();
            freeJoints.addAll(attackingJoints);
        }
    }

    protected Checker getCheckerByXYZ(Vector3f v, ArrayList<Checker> checkers) {
        for (Checker c : checkers) {
            if (c.x == Math.round(v.x) && c.y == Math.round(v.y) &&
                c.z == Math.round(v.z)) return c;
        }
        return null;
    }

    protected Checker getMyCheckerByGeometryName(String GeoName) {
        for (Checker c: mycheckers) if (c.geom.getName().equals(GeoName)) return c;
        return null;
    }

    private HashMap<Checker,ArrayList<Vector3f>> getAttackedMovesMap(HashMap<Checker,ArrayList<Vector3f>> movesMap) {
        HashMap<Checker,ArrayList<Vector3f>> attackedMovesMap = new HashMap();
        attackedMovesMap.putAll(movesMap);
        attackedMovesMap.keySet().retainAll(getAttackedCheckers(movesMap));
        return attackedMovesMap;
    }

     private boolean findDangerAroundAfterMove(Checker me, Vector3f move) {
        // searches at least one "ai" around "move" point with opposite empty joint, excluding point,
        // where is Checker "me" before moving, (because after move this point will be empty )
        for (Checker ai: aicheckers) {
            if ((isChecker(ai, new Vector3f(move.x + 2, move.y, move.z + 2)) & areNoCheckersExcludingMe(me, new Vector3f(move.x - 2, move.y, move.z - 2))) |
                (isChecker(ai, new Vector3f(move.x - 2, move.y, move.z + 2)) & areNoCheckersExcludingMe(me, new Vector3f(move.x + 2, move.y, move.z - 2))) |
                (isChecker(ai, new Vector3f(move.x + 2, move.y, move.z - 2)) & areNoCheckersExcludingMe(me, new Vector3f(move.x - 2, move.y, move.z + 2))) |
                (isChecker(ai, new Vector3f(move.x - 2, move.y, move.z - 2)) & areNoCheckersExcludingMe(me, new Vector3f(move.x + 2, move.y, move.z + 2))) |
                (isChecker(ai, new Vector3f(move.x, move.y + 2, move.z + 2)) & areNoCheckersExcludingMe(me, new Vector3f(move.x, move.y - 2, move.z - 2))) |
                (isChecker(ai, new Vector3f(move.x, move.y - 2, move.z + 2)) & areNoCheckersExcludingMe(me, new Vector3f(move.x, move.y + 2, move.z - 2))) |
                (isChecker(ai, new Vector3f(move.x, move.y + 2, move.z - 2)) & areNoCheckersExcludingMe(me, new Vector3f(move.x, move.y - 2, move.z + 2))) |
                (isChecker(ai, new Vector3f(move.x, move.y - 2, move.z - 2)) & areNoCheckersExcludingMe(me, new Vector3f(move.x, move.y + 2, move.z + 2)))) return true;
        }
        return false;
    }

     private HashMap<Checker,ArrayList<Vector3f>> getCanNotBeAvengedMap(HashMap<Checker,ArrayList<Vector3f>> movesMap) {
        //HashMap<Checker,ArrayList<Vector3f>> notAvengedMap = new HashMap();
        movesMap.keySet().removeAll(getCheckersCanBeAvenged(movesMap));
        return movesMap;
    }

     private HashMap<Checker,ArrayList<Vector3f>> getSafeMovesMap(HashMap<Checker,ArrayList<Vector3f>> movesMap) { //smarlevel 2 (2^1)
        HashMap<Checker,ArrayList<Vector3f>> safeMovesMap = new HashMap<>();
        //System.out.println("getSafeMovesMap movesMap " + movesMap );
        for (Entry<Checker,ArrayList<Vector3f>> entry: movesMap.entrySet()) {
            ArrayList<Vector3f> safeMoves = new ArrayList<>();
            for (Vector3f move: entry.getValue()) {
                if (isMoveSafe(entry.getKey(), move, aicheckers)) {
                    safeMoves.add(move);
                    //System.out.println(" AroundAfterMove  checker " + entry.getKey().v + " d move   " + v);
                }
            }
            if (!safeMoves.isEmpty()) safeMovesMap.put(entry.getKey(), safeMoves);
        }
        //System.out.println("getSafeMovesMap safeMovesMap " + safeMovesMap );
        return safeMovesMap;
    }

     private boolean isMoveSafe(Checker me, Vector3f move, ArrayList<Checker> checkers) {
        // searches at least one "ai" around Checker "me" with opposite empty joint
        for (Checker ai: checkers) {
            boolean oneIs = false;
            for (Vector3f v: directions) {
                oneIs = oneIs | (isChecker(ai, new Vector3f(move.add(v))) & areNoCheckersExcludingMe(me,new Vector3f(move.add(v.mult(-1))))) ;
                //System.out.println(me + " " + me.add(v) + " " + me.add(v.mult(-1)) + " v " + v  + " -v " + v.mult(-1) + " p " + p);
            }
            if (oneIs) return false;
        }
        return true;
    }

    private boolean isDangerAround(Vector3f me, ArrayList<Checker> checkers) {
        // searches at least one "ai" around Checker in joint "me" with opposite empty joint
        for (Checker ai: checkers) {
            boolean oneIs = false;
            for (Vector3f d: directions) {
                oneIs = oneIs | (isChecker(ai, new Vector3f(me.add(d))) & areNoCheckers(new Vector3f(me.add(d.mult(-1))))) ;
                //System.out.println(me + " " + me.add(v) + " " + me.add(v.mult(-1)) + " v " + v  + " -v " + v.mult(-1) + " p " + p);
            }
            if (oneIs) return true;
        }
        return false;
    }



    private boolean isAppearedDanger(Checker willMoved) {
        for (Checker me: mycheckers ) {
            if (isDangerAroundD(willMoved, me.v, aicheckers )) return true;
        }
        return false;
    }

    private boolean isDangerAroundAfterEatn(Checker me, Vector3f move) {
        // searches at least one "ai" around "move" point with opposite empty joint,
        // Checker "me" will  not in this point in any case
        ArrayList<Vector3f> usedDir = new ArrayList(directions);
        usedDir.remove(me.v.subtract(move));
        for (Checker ai: aicheckers) {
            for (Vector3f d: usedDir) {
               // xheck directions isChecker(ai, new Vector3f(move.add(d)));
            }
            if ((isChecker(ai, new Vector3f(move.x + 2, move.y, move.z + 2)) & areNoCheckersExcludingMe(me, new Vector3f(move.x - 2, move.y, move.z - 2))) |
                (isChecker(ai, new Vector3f(move.x - 2, move.y, move.z + 2)) & areNoCheckersExcludingMe(me, new Vector3f(move.x + 2, move.y, move.z - 2))) |
                (isChecker(ai, new Vector3f(move.x + 2, move.y, move.z - 2)) & areNoCheckersExcludingMe(me, new Vector3f(move.x - 2, move.y, move.z + 2))) |
                (isChecker(ai, new Vector3f(move.x - 2, move.y, move.z - 2)) & areNoCheckersExcludingMe(me, new Vector3f(move.x + 2, move.y, move.z + 2))) |
                (isChecker(ai, new Vector3f(move.x, move.y + 2, move.z + 2)) & areNoCheckersExcludingMe(me, new Vector3f(move.x, move.y - 2, move.z - 2))) |
                (isChecker(ai, new Vector3f(move.x, move.y - 2, move.z + 2)) & areNoCheckersExcludingMe(me, new Vector3f(move.x, move.y + 2, move.z - 2))) |
                (isChecker(ai, new Vector3f(move.x, move.y + 2, move.z - 2)) & areNoCheckersExcludingMe(me, new Vector3f(move.x, move.y - 2, move.z + 2))) |
                (isChecker(ai, new Vector3f(move.x, move.y - 2, move.z - 2)) & areNoCheckersExcludingMe(me, new Vector3f(move.x, move.y + 2, move.z + 2)))) return true;
        }
        return false;
    }

    protected void  makeBench() {
        this.placesOfBench.clear();
        for (int i = -dimensionZ - 7; i < aicheckers.size(); i++) this.placesOfBench.add(i);
    }

     private void selectEatChain(){
        if (canEat.isEmpty()) return;
        for (Entry<Checker,ArrayList<Vector3f>> entry: canEat.entrySet()) {
            entry.getKey().eatChains.clear();
            for (int i = 0; i < entry.getValue().size(); i = i+2) {
                findEatChains(entry.getKey(), entry.getValue().get(i), entry.getValue().get(i + 1));
            }
            System.out.println("chains " + entry.getKey().eatChains);
        }
    }

    private void findEatChains(Checker checkerToEat, Vector3f toBeEated, Vector3f start) {
        // searches food chains AFTER FIRST eat 
        for (Vector3f d: directions) {
            if (!d.equals(checkerToEat.v.subtract(toBeEated))) {
            Vector3f food = start.add(d);
                while (isJointIn(food)) {
                    if (!areNoCheckers(food)){ // if are checkers in food point
                        for (Checker me: mycheckers) {
                            if (isChecker(me, food)) break;
                        }
                        for (Checker ai: aicheckers) {
                        //System.out.println("xx " + ai.v + " food " + food + " " + food.add(v));
                            if (isChecker(ai, food)) {
                                Vector3f fin = food.add(d);
                                ArrayList<Vector3f> foodChain = new ArrayList();
                                while (isJointIn(fin)) {
                                    if (!areNoCheckers(fin)) break;
                                    foodChain.add(food);
                                    foodChain.add(fin);
                                    findEatChains(checkerToEat, food, fin);
                                    checkerToEat.eatChains.add(foodChain);
                                    if (!checkerToEat.damka) break;
                                    fin.add(d);
                                }
                            }
                        }
                    }
                    if (!checkerToEat.damka) break;
                    food = food.add(d); // look next joint
                }
            }
        }

    }
/*     public void doMyMoveM(Joint pickedJoint, Checker pickedChecker) {
        //if (pickedJoint != null) pickedJoint.resetColor();
        if (areNoCheckers(pickedJoint.v)) {
            //if (pickedChecker.damka) doMyMoveDamka(pickedJoint, pickedChecker);
            //else {
            if (Math.abs(pickedJoint.z - pickedChecker.z) == 2 &
                ((Math.abs(pickedChecker.x - pickedJoint.x) == 0 & 
                Math.abs(pickedChecker.y - pickedJoint.y) == 2) ^
                (Math.abs(pickedChecker.x - pickedJoint.x) == 2 & 
                Math.abs(pickedChecker.y - pickedJoint.y) == 0))) {
                if (!haveEat) moving(pickedJoint, pickedChecker);              //prostoi hod
            }
               
            if (Math.abs(pickedChecker.z - pickedJoint.z) == 4 & // hod s edoj
                    (Math.abs(pickedChecker.y - pickedJoint.y) == 0 &
                     Math.abs(pickedChecker.x - pickedJoint.x) == 4) ^ 
                    (Math.abs(pickedChecker.y - pickedJoint.y) == 4 &
                     Math.abs(pickedChecker.x - pickedJoint.x) == 0)) {  
                    checkerToBeEated = getCheckerByXYZ(new Vector3f(
                        pickedChecker.x + (pickedJoint.x - pickedChecker.x)/2,
                        pickedChecker.y + (pickedJoint.y - pickedChecker.y)/2, 
                        pickedChecker.z + (pickedJoint.z - pickedChecker.z)/2), aicheckers);
                    if (checkerToBeEated != null) eating(pickedJoint, pickedChecker);
                }
            //}
        }    
    }
 
    
        
     
    
    
    private void moving(Joint pickedJoint, Checker pickedChecker){
        moveFrom = convertToAa1(pickedChecker.v);
        moveTo = convertToAa1(pickedJoint.v);
        if (pickedJoint.v.z == -pickedChecker.startZ & !pickedChecker.mamka) returnChecker = true;
        pickedChecker.setMove(pickedJoint.v, damkaAllowed);
        pickedChecker.resetColor();
        checkerToMove = pickedChecker;
        moveDid = true;
        moving = true;
        passMove = true;
        resetGame();
        if (returnChecker) returnCheckerToPlay();
        if (soundOn) soundEffects.pick_audio.playInstance();
    }
    
   
    private void eating(Joint pickedJoint, Checker pickedChecker) {

        moveFrom = convertToAa1(pickedChecker.v);
        debrisVector.set(checkerToBeEated.v);
        int place = benchOfEatedCheckers.size();
        checkerToBeEated.setMove(new Vector3f(place - dimensionX, 
                    10, checkerToBeEated.startZ + topbot * 10), true); // set remove vector , not yet moving
        benchOfEatedCheckers.add(checkerToBeEated);
            resetEffects();    
        if (pickedJoint.v.z == -pickedChecker.startZ & !pickedChecker.mamka) returnChecker = true;
        pickedChecker.setMove(pickedJoint.v, damkaAllowed);
        moveTo = convertToAa1(pickedJoint.v);

        moveating = true;
        eating = true;
        getEatVectors(pickedChecker);
        if (!pickedChecker.eatList.isEmpty()) {
            checkerToMove = pickedChecker;
            randomEat(pickedChecker.eatList);
            moveDid = true;
            passMove = false;
            moveChecked = false;    
            return;
        
        }
        pickedChecker.resetColor();
        moveDid = true;
        passMove = true;
        if (returnChecker) returnCheckerToPlay();
        resetGame();
        if (soundOn) soundEffects.pick_audio.playInstance();     
    }*/

     // manual game with damka
    /*private void doMyMoveDamka(Joint pickedJoint, Checker pickedChecker) {
        if (areNoCheckers(pickedJoint.v)) {
            if (pickedJoint.z != pickedChecker.z &
                (Math.abs(pickedChecker.y - pickedJoint.y) == Math.abs(pickedChecker.z - pickedJoint.z) &
                pickedChecker.x - pickedJoint.x == 0) ^
                (Math.abs(pickedChecker.x - pickedJoint.x) == Math.abs(pickedChecker.z - pickedJoint.z) & 
                pickedChecker.y - pickedJoint.y == 0)) {
                switch(checkLine(pickedJoint.v, pickedChecker.v)) {
                    case -1:
                    break;
                    case 0:
                        moving(pickedJoint, pickedChecker);
                    break;
                    case 1:
                        eating(pickedJoint, pickedChecker);                     //can eat
                    break;
                    case 2:
                    break;
                    case 3:
                    break;
                }
            }
        }
    }*/

    /*
    public ArrayList<Vector3f> findMoveVectors(Checker checker, int up){
        ArrayList<Vector3f> vl = new ArrayList<>(); 
        Vector3f ve = new Vector3f();       
        if (areNoCheckers(ve.set(checker.x + 2, checker.y, checker.z + 2 * up))) vl.add(ve);
        Vector3f vw = new Vector3f(); 
        if (areNoCheckers(vw.set(checker.x - 2, checker.y, checker.z + 2 * up))) vl.add(vw);
        Vector3f vn = new Vector3f();
        if (areNoCheckers(vn.set(checker.x, checker.y + 2, checker.z + 2 * up))) vl.add(vn);
        Vector3f vs = new Vector3f();
        if (areNoCheckers(vs.set(checker.x, checker.y - 2, checker.z + 2 * up))) vl.add(vs);
        //System.out.println("ch " + checker.v + " moves " + vl.toString());
        return vl;
    }
    */
      /*
    protected ArrayList<Vector3f> findEatVectors(Checker c){
        ArrayList<Vector3f> vv = new ArrayList<>();  // list of eats
        Vector3f[] vc = new Vector3f[8] ;  // checker vector 
        Vector3f[] vf = new Vector3f[8] ;  // free vector
        for (int i = 0; i < 8; i++) {
            vc[i] = new Vector3f();
            vf[i] = new Vector3f();
        }
        for (Checker ai: aicheckers) { 
        
            if (isChecker(ai, vc[0].set(c.x + 2, c.y, c.z + 2))) {
                if (areNoCheckers(vf[0].set(c.x + 4, c.y, c.z + 4))) {
                    vv.add(vc[0]);
                    vv.add(vf[0]);
                    ai.mayBeEated = true;
                }
            }              
            if (isChecker(ai, vc[1].set(c.x - 2, c.y, c.z + 2))) {
                if (areNoCheckers(vf[1].set(c.x - 4, c.y, c.z + 4))) {
                    vv.add(vc[1]);
                    vv.add(vf[1]);
                    ai.mayBeEated = true;
                }
            }
            if (isChecker(ai, vc[2].set(c.x, c.y + 2, c.z + 2))) {
                if (areNoCheckers(vf[2].set(c.x, c.y + 4, c.z + 4))) {
                    vv.add(vc[2]);
                    vv.add(vf[2]);
                    ai.mayBeEated = true;
                }
            }              
            if (isChecker(ai, vc[3].set(c.x, c.y - 2, c.z + 2))) {
                if (areNoCheckers(vf[3].set(c.x, c.y - 4, c.z + 4))) {
                    vv.add(vc[3]);
                    vv.add(vf[3]);
                    ai.mayBeEated = true;
                }
            }
            if (isChecker(ai, vc[4].set(c.x + 2, c.y, c.z - 2))) {
                if (areNoCheckers(vf[4].set(c.x + 4, c.y, c.z - 4))) {
                    vv.add(vc[4]);
                    vv.add(vf[4]);
                    ai.mayBeEated = true;
                }
            }              
            if (isChecker(ai, vc[5].set(c.x - 2, c.y, c.z - 2))) {
                if (areNoCheckers(vf[5].set(c.x - 4, c.y, c.z - 4))) {
                    vv.add(vc[5]);
                    vv.add(vf[5]);
                    ai.mayBeEated = true;
                }
            }
            if (isChecker(ai, vc[6].set(c.x, c.y + 2, c.z - 2))) {
                if (areNoCheckers(vf[6].set(c.x, c.y + 4, c.z - 4))) {
                    vv.add(vc[6]);
                    vv.add(vf[6]);
                    ai.mayBeEated = true;
                }
            }              
            if (isChecker(ai, vc[7].set(c.x, c.y - 2, c.z - 2))) {
                if (areNoCheckers(vf[7].set(c.x, c.y - 4, c.z - 4))) {
                    vv.add(vc[7]);
                    vv.add(vf[7]);
                    ai.mayBeEated = true;
                }
            }
        }
        //System.out.println("findEatVectors ch " + c.v + " eat " + vv.toString() + 
        //        " ai" + aicheckers.size()+ " my" + mycheckers.size() + Main.hod);    
        return vv;  
    }
   */
     /*protected HashMap<Checker, ArrayList<Vector3f>> canMeEat(){
        HashMap<Checker, ArrayList<Vector3f>> eatMap = new HashMap<>();
        ArrayList<Vector3f> eatList ;
        for (Checker c: mycheckers) {
            eatList = canCheckerEat(c);
            if (!eatList.isEmpty()) eatMap.put(c, eatList);
        }
        return eatMap;
    }
    
    public HashMap<Checker, ArrayList<Vector3f>> canMeMove() {
        HashMap<Checker, ArrayList<Vector3f>> ca = new HashMap<>();
        ArrayList<Vector3f> vl ;
        for (Checker c: mycheckers) {
            vl = findJointsForMove(c, 1); 
            if (!vl.isEmpty()) ca.put(c, vl);
        }
    return ca;    
    }   */
    /*
    public void burstEated(Vector3f eated, int bax) {  
        int i = 0;
        if (soundOn) soundEffects.heart_audio.playInstance();
        for (Checker c : aicheckers) {
            if (c.v.equals(eated)) {
                checkerToBeEated = aicheckers.get(i);
                eating = true;
                break;
            }  
            i++;
        }       
        if (bax == 0) {
            spark = true;
            checkerToBeEated.remove();
            resetEffects();    
            if (soundOn) soundEffects.burst_audio.playInstance(); //for manual game only
        } 
    } */
    
    /*private HashMap<Checker,ArrayList<Vector3f>> thinkMoreAboutMove(HashMap<Checker,ArrayList<Vector3f>> movesMap){
        //System.out.println("movesMap " + movesMap);
        HashMap<Checker,ArrayList<Vector3f>> allMovesMap = new HashMap();
        allMovesMap.putAll(movesMap);
        HashMap<Checker,ArrayList<Vector3f>> attackedCheckersMap= getAttackedMovesMap(allMovesMap);
        //System.out.println("attackedCheckersMap " + attackedCheckersMap);
        if (!attackedCheckersMap.isEmpty()) {
            HashMap<Checker,ArrayList<Vector3f>> notAvengedMap = getCanNotBeAvengedMap(attackedCheckersMap);
            if(!notAvengedMap.isEmpty()) {
                HashMap<Checker,ArrayList<Vector3f>> safeMovesMap = getSafeMovesMap(notAvengedMap);
                if (!safeMovesMap.isEmpty()) return  safeMovesMap;
                else return notAvengedMap;
            }
            allMovesMap.keySet().removeAll(attackedCheckersMap.keySet()); // if all attacked may be avenged 
        }
      
        HashMap<Checker,ArrayList<Vector3f>> safeMovesMap = getSafeMovesMap(allMovesMap);
        //System.out.println("safeMoveMap " + safeMovesMap) ;
        if (!safeMovesMap.isEmpty()) {
            HashMap<Checker,ArrayList<Vector3f>> movesToDamkaMap = getMovesToDamkaMap(safeMovesMap);
            if (!movesToDamkaMap.isEmpty()) return movesToDamkaMap;
            safeMovesMap = getDangerForNeighborsAfterMoveMap(safeMovesMap);
            HashMap<Checker,ArrayList<Vector3f>> movesWithFuture = getMapWithFutureMoves(safeMovesMap);
            //System.out.println("movesWithFuture " + movesWithFuture) ;
            if (!movesWithFuture.isEmpty()) return movesWithFuture; 
            return safeMovesMap;
        }
        else return allMovesMap;
        
    }*/
}
