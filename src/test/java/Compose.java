import java.util.ArrayList;
import java.util.List;

public class Compose {

    private Integer[] digitArr = new Integer[] {1988, 1890, 989, 899, 888, 389, 360, 289};
    private int totalMix = 31798;
    private int length;
    private int count;
    private List<List<Integer>> combination = new ArrayList<>();

    public void compose() {
        long start = System.currentTimeMillis();
        length = digitArr.length;
        ComposeList<Integer> composeList = this.new ComposeList<>();
        test(composeList, 0);
        long end = System.currentTimeMillis();
        long cost = (end-start)/1000;
        System.out.println(String.format("组合测试完成!\n耗时：%s秒；尝试数：%s；组合数：%s；\n组合内容：%s", cost, count, combination.size(), "[]"));
    }

    private void test(ComposeList<Integer> cList, int index_s) {
        for (int i=index_s; i<length; i++) {
            int res = cList.addCell(digitArr[i]);
            if (res == 1) {
                test(cList, i);
            }
        }
        cList.removeLast();
    }

    public static void main(String[] args) {

        Compose compose = new Compose();
        compose.compose();

    }

    class ComposeList<I extends Number> extends ArrayList {

        private int sum;

        public int addCell(int num) {
            count++;
            this.add(num);
//            System.out.println(String.format("尝试组合：%s=%s", this, this.sum));
            if (this.sum < totalMix) {
                return 1;
            } else if (this.sum > totalMix) {
                this.removeLast();
                return -1;
            } else {
                List<Integer> cell = new ArrayList<>(this);
                System.out.println("--------------------------------组合成功：" + this);
                combination.add(cell);
                this.removeLast();
                return 0;
            }
        }

        public void removeLast() {
            if (this.size() > 0) {
                this.remove(this.size() - 1);
            }
        }

        @Override
        public boolean add(Object o) {
            this.sum += (int) o;
            return super.add(o);
        }

        @Override
        public Object remove(int index) {
            Object obj = super.remove(index);
            this.sum -= (int) obj;
            return obj;
        }

    }

}


