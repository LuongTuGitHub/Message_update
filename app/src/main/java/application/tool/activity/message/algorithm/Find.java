package application.tool.activity.message.algorithm;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class Find {

    /**
     * Lấy ra độ dài các xâu con chung liên tiếp của 2 {@code String} và thêm
     * {@code index} vào số cuối của {@code ArrayList} trả ve
     *
     * @param a     String thứ nhất
     * @param b     String thứ hai
     * @param index phần tử cuối cùng của {@code ArrayList} trả ve
     * @return {@code ArrayList} gồm các số nguyên là độ dài của các xâu con chung
     * liên tiếp đã được sắp xếp giảm dần và kết thúc bằng {@code index}
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Integer> getLengthSub(String a, String b, int index) {
        int A = a.length();
        int B = b.length();
        ArrayList<Integer> res = new ArrayList<>();
        int[][] m = new int[A + 1][B + 1];
        for (int i = 1; i <= A; i++) {
            for (int j = 1; j <= B; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    m[i][j] = 1 + m[i - 1][j - 1];
                    res.add(m[i][j]);
                    res.remove(Integer.valueOf(m[i][j] - 1));
                } else
                    m[i][j] = 0;
            }
        }
        res.sort((o1, o2) -> o2.compareTo(o1));
        res.add(index);
        return res;
    }

    /**
     * Lấy ra kết quả tìm kiếm của {@code name} trong {@code nameList}
     *
     * @param name     tên cần tìm
     * @param nameList {@code ArrayList} chứa tất cả tên
     * @return {@code ArrayList} chứa những tên mà @author nghĩ là đúng
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<String> findByName(String name, ArrayList<String> nameList) {
        ArrayList<ArrayList<Integer>> priority = new ArrayList<>();
        for (int i = 0; i < nameList.size(); i++) {
            priority.add(getLengthSub(name, nameList.get(i), i));
        }
        priority.sort((o1, o2) -> {
            int i = 0;
            while (o1.get(i).equals(o2.get(i)) && i + 1 < o1.size() && i + 1 < o2.size())
                i++;

            return o2.get(i).compareTo(o1.get(i));
        });
        ArrayList<String> res = new ArrayList<>();
        for (int i = 0; i < nameList.size(); i++) {
            if (priority.get(i).get(0) > 2)
                res.add(nameList.get(priority.get(i).get(priority.get(i).size() - 1)));
        }
        return res;
    }

}