class Solution {

    public String fun1(){
        return "123";
    }
    public String fun2(){
        return "abc";
    }

    public boolean equals(){
        return true;
    }

    public static void main(String[] args)
    {
        Solution solution;
        solution.fun1() == solution.fun2();
        int ss = 0;
        for(String s = "abc";; s== ss){
        }
    }
}