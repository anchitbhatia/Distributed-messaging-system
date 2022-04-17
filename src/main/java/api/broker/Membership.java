package api;

import java.util.concurrent.ConcurrentLinkedDeque;

public class Membership {
    private final ConcurrentLinkedDeque<Integer> members;

    public Membership() {
        this.members = new ConcurrentLinkedDeque<>();
    }

    public void addMember(Integer id){
        if (!checkMember(id)) {
            members.add(id);
        }
    }

    public void removeMember(Integer id){
        members.remove(id);
    }

    public boolean checkMember(Integer id){
        return members.contains(id);
    }

    public ConcurrentLinkedDeque<Integer> getMembers(){
        return members;
    }
}
