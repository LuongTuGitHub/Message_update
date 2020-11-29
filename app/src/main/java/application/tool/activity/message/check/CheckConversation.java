package application.tool.activity.message.check;

import java.util.ArrayList;

import application.tool.activity.message.object.PersonInConversation;

public class CheckConversation {
    public boolean conversationExist(ArrayList<PersonInConversation> person, ArrayList<ArrayList<PersonInConversation>> list) {
        if (person.size() > 2) {
            return true;
        }
        sortList(person);
        for (int i = 0; i < list.size(); i++) {
            sortList(list.get(i));
        }
        for (int i = 0; i < list.size(); i++) {
            if ((person.get(0).getPerson().equals(list.get(i).get(0).getPerson())) && (person.get(1).getPerson().equals(list.get(i).get(1).getPerson()))) {
                return false;
            }
        }

        return true;
    }

    public void sortList(ArrayList<PersonInConversation> person) {
        for (int i = 0; i < person.size() - 1; i++) {
            for (int j = i + 1; j < person.size(); j++) {
                if (person.get(i).getPerson().compareTo(person.get(j).getPerson()) > 0) {
                    PersonInConversation personInConversation = person.get(i);
                    person.set(i, person.get(j));
                    person.set(j, personInConversation);
                }
            }
        }

    }
}
