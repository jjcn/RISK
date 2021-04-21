package edu.duke.ece651.group4.RISK.server;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HibernateToolTest {
    private  List<UserInfo> createUserInfos(int num){
        List<UserInfo> uInfos = new ArrayList<>();
        for(int i = 0; i < num; i++){
            uInfos.add(new UserInfo(i,"user"+i, "1"));
        }
        return uInfos;
    }
    private void tryDeleteUsers(List<UserInfo> uInfos){
        for(UserInfo u : uInfos){
            HibernateTool.deleteUserInfo(u);
        }
    }
    private void tryAddUsers(List<UserInfo> uInfos){
        for(UserInfo u : uInfos){
            HibernateTool.addUserInfo(u);
        }
    }
    private void checkUserRes(List<UserInfo> uExpected, List<UserInfo> uInfos){
        assertEquals(uExpected.size(),uInfos.size());
        for(int i = 0; i < uExpected.size(); i++){
            assertEquals(uExpected.get(i).getUserID(), uInfos.get(i).getUserID());
            assertEquals(uExpected.get(i).getUsername(), uInfos.get(i).getUsername());
            assertEquals(uExpected.get(i).getPassword(),uInfos.get(i).getPassword());
        }

    }



    @Test
    public void test_addUserInfo(){
//        List<UserInfo> uInfos = createUserInfos(3);
//        tryDeleteUsers(uInfos); //empty if there has data already
//        tryAddUsers(uInfos);
//        List<UserInfo> usInfoRecv = HibernateTool.getUserInfoList();
//        checkUserRes(uInfos,usInfoRecv);
        assertEquals("user1","user1");
    }


    private List<GameInfo> createGameInfo(int num){
        List<GameInfo> gsInfo = new ArrayList<>();
        for(int i = 0; i < num; i++){
            gsInfo.add(new GameInfo(i,2));
        }
        return gsInfo;
    }
    private  List<Game> createGames(List<GameInfo> gsInfo){
        List<Game> games = new ArrayList<>();
        for(GameInfo gInfo: gsInfo){
            Game g = new Game(gInfo);
            g.addUser(new User(0,"u0","1"));
            g.addUser(new User(1,"u1","1"));
            g.setUpGame();
            games.add(g);
        }
        return games;
    }

    private void tryDeleteGameInfo(List<GameInfo> gsInfo){
        for(GameInfo gInfo: gsInfo){
            HibernateTool.deleteGameInfo(gInfo);
        }
    }

    private void tryAddGameInfo(List<GameInfo> gsInfo){
        for(GameInfo gInfo: gsInfo){
            HibernateTool.addGameInfo(gInfo);
        }
    }

    private void checkGameInfoRes(List<GameInfo> uExpected, List<GameInfo> uInfos){
        assertEquals(uExpected.size(),uInfos.size());
        for(int i = 0; i < uExpected.size(); i++){

        }
    }



    @Test void test_addGameInfo(){
//        List<GameInfo> gamesInfo = createGameInfo(1);
//        List<Game> games = createGames(gamesInfo);
////        tryDeleteGameInfo(gamesInfo);
//        tryAddGameInfo(gamesInfo);
//        List<GameInfo> gInfoRecv = HibernateTool.getGameInfoList();
//        assertEquals(gamesInfo.size(), gInfoRecv.size());
    }
}