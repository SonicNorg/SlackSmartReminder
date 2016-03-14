package com.grizzly.studio.slacksmartreminder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

import com.ullink.slack.simpleslackapi.*;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.impl.*;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;

public class Main {
    static LinkedList<UserItem> userList = new LinkedList<>();
    static UserItem currentUser = null;
    static SlackSession session = null;
    static ArrayList<String> commandList = new ArrayList<>();
    static SlackUser me = null;
    Calendar time = Calendar.getInstance();

    public Main() {
        setTime();
    }

    public static void main(String[] args) {
        Main app = new Main();
        try {
            commandList.add("!команды");
            app.connect();

            app.addUser("maksim.budyko");
            app.addUser("sergey.ermokhin");
            app.addUser("maxim.zelensky");
            app.addUser("norg");
            app.addUser("evgeniy.sokolov");
            app.addUser("daniilut");
            app.addUser("tatyana.chusovlyanova");
            app.setCurrentUser("tatyana.chusovlyanova");
            app.setUserEnabled("norg", false);

            app.session.addMessagePostedListener(new SlackMessagePostedListener() {
                @Override
                public void onEvent(SlackMessagePosted event, SlackSession session) {
                    //todo interface Command, class CommandParser, etc.
                    if (event.getSender().equals(me))
                        return;
                    String lowerCaseMessage = event.getMessageContent().toLowerCase();
                    if(event.getMessageContent().toLowerCase().startsWith("!добавить"))
                        session.sendMessage(event.getChannel(), "Ручное добавление пока не поддерживается.");
                    else if(event.getMessageContent().toLowerCase().startsWith("!удалить"))
                        session.sendMessage(event.getChannel(), "Удаление пока не поддерживается.");
                    else if(event.getMessageContent().toLowerCase().startsWith("!включить"))
                        session.sendMessage(event.getChannel(), "Включение/выключение пользователей пока не поддерживается.");
                    else if(event.getMessageContent().toLowerCase().startsWith("!выключить"))
                        session.sendMessage(event.getChannel(), "Включение/выключение пользователей пока не поддерживается.");
                    else if(event.getMessageContent().toLowerCase().startsWith("!команды"))
                        session.sendMessage(event.getChannel(), app.getCommandList());
                    else if(lowerCaseMessage.contains("кто проводит стендап"))
                        session.sendMessage(event.getChannel(), app.getUserList());
                    else if(lowerCaseMessage.contains("кто сегодня проводит стендап"))
                        session.sendMessage(event.getChannel(), "Сегодня стендап проводит(-л) " + session.findUserByUserName(currentUser.getName()).getRealName()); //todo getUserRepresentation()
                    else if(event.getMessageContent().toLowerCase().contains("кто завтра проводит стендап"))
                        session.sendMessage(event.getChannel(), "Завтра стендап проводит " + session.findUserByUserName(app.getNextUser().getName()).getRealName());
                }
            });
        }catch (IOException e) {
            System.err.println(e);
        }
    }

    private String getCommandList() {
        StringBuilder sb = new StringBuilder();
        for (String s:commandList) {
            sb.append(s);
            sb.append("\n");
        }

        return sb.toString();
    }

    public UserItem getNextUser() {
        UserItem u;
        int curNum = -1;
        for (int i=0; i<userList.size()-1; i++) {
            if(userList.get(i) == currentUser) {
                curNum = i;
                break;
            }
        }
        boolean keep = true;
        int k = curNum+1;
        while(keep || k <= curNum) {
            if (k >= userList.size()) {
                k = 0;
                keep = false;
            }
            u = userList.get(k++);
            if (u.isEnabled())
                return u;
        }

        return null;
    }

    public boolean setUserEnabled(String userName, boolean enabled) {
        //todo check whether disabling current user
        UserItem user = findByName(userName);
        if(user == null)
            return false;
        user.setEnabled(enabled);
        return true;
    }

    public String addUser(String userName, int index) {
        userList.add(index, new UserItem(userName));
        if(index == 0)
            return "";
        UserItem prev = userList.get(index - 1);
        return prev.getName();
    }

    public String addUser(String userName) {
        return addUser(userName, userList.size());
    }

    public void connect() throws IOException {
        session = SlackSessionFactory.createWebSocketSlackSession("xoxb-26546563778-Q1t3Xl3KQqyQb02ED4aX8BgS");
        session.connect();
        me = session.findUserByUserName("whosurdaddy");
    }

    public UserItem findByName(String userName) {
        for (UserItem u:userList)
            if(u.getName().equals(userName))
                return u;
        return null; //todo implement NULL pattern
    }

    public String getUserList() {
        StringBuilder sb = new StringBuilder();
        for (UserItem u:userList) {
            sb.append(String.format("%c\t%s\n", currentUser == u?'\u2713':u.isEnabled()?'\u2605':'\u2606', session.findUserByUserName(u.getName()).getRealName()));
        }

        return sb.toString();
    }

    public boolean setCurrentUser(String userName) {
        UserItem user = findByName(userName);
        if (user == null)
            return false;
        setCurrentUser(user);
        return true;
    }

    public void setCurrentUser(UserItem user) {
        currentUser = user;
    }

    public void setTime(int hour, int minute) {
        time.setTimeInMillis(System.currentTimeMillis());
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, minute); //todo add validation
    }

    public void setTime() {
        time.setTimeInMillis(System.currentTimeMillis());
        time.set(Calendar.HOUR_OF_DAY, 10);
        time.set(Calendar.MINUTE, 45);
    }
}
