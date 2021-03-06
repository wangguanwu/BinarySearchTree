package com.szdx.redblack;

import javax.xml.soap.Node;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.*;

public class RedBlackTree<K,V> {
    private RBTNode<K,V> root ;
    private Comparator<K> comparator = null;
    public RedBlackTree(){
        root = null ;
    }

    public void setComparator( Comparator<K> comparator ) {
        this.comparator = comparator;
    }

    public Comparator<K> getComparator() {
        return comparator;
    }

    public RBTNode getRoot() {
        return root;
    }
    public RBTNode<K,V> successor( RBTNode<K,V> s){//寻找后继
        if( s.right != null ){
            return minimum( s.right ) ;
        }
        RBTNode<K,V> y = s.parent ;
        while( y != null && s == y.right ){
            s =  y;
            y = y.parent ;
        }
        return y ;
    }
    private RBTNode<K,V> minimum( RBTNode<K,V> r) {//找到r的最小节点
        while( r.left!=null ){
            r = r.left ;
        }
        return r ;
    }

    public RBTNode<K,V> deleteNode( RBTNode<K,V> z ){
//        RBTNode<K,V> y = null , x = null ;
//        if( z.left == null || z.right == null )//如果z的左孩子为空或者右孩子为空，将z赋值给y
//            y = z ;
//        else//如果存在左右孩子，那么把z的后继节点赋予y
//            y = successor( z ) ;
//        if( y.left != null ){
//            x = y.left ;
//        }else
//            x = y.right ;
//        if( x != null )
//             x.parent = y.parent ;//将y的父母节点设为x的父母节点
//        if( y.parent == null )//如果y的父母节点是空，那么y就是树根
//            root = x ;//将x设为树根
//        else if( y == y.parent.left )//如果y为y父母的左孩子
//            y.parent.left = x ;//设x为y的父母的左孩子
//        else
//            y.parent.right = x ;//如果y是y父母的右孩子那么将y父母的右孩子设为x
//        if ( y!= x ){//只拷贝值
//            z.key = y.key;
//            z.value = y.value ;
//        }
//        if( y.color.equals("black"))//如果是黑色节点
//
//             return y ;
//        return null ;
        RBTNode<K,V> y = z ;
        RBTNode<K,V> x = null ;
        RBTNode<K,V> x_parent = null ;//保存x的父节点，因为如果x为空，那么x无法访问父节点
        String y_orginal_color = y.color ;//保存y的最初的颜色，因为最后是删除y的颜色
        if ( z.left == null ) {//如果左子树是空
            x = z.right ;
            transplant( z , z.right );//把z的右子树移植到z的位置
            x_parent = z.parent ;
        }
        else if ( z.right == null ){//右子树为空
            x = z.left ;
            x_parent = z ;
            transplant(z , z.left );//左子树不为空，右子树为空，把左子树移植到z的位置
            x_parent = z.parent;
        }else{//左右子树都不为空
            y = minimum( z.right ) ;//y为z的后继,y的左子树一定为空
            y_orginal_color = y.color ;
            x = y.right ;
            if( y.parent == z ){//如果y是z的右孩子
                if (x != null)
                    x.parent = y;
                    x_parent = y ;
            }
            else {//如果y不是z的右孩子
                    transplant(y, y.right);//移植y的右孩子到y的位置
                    x_parent = y.parent ;
                    y.right = z.right ;//y的右孩子设为z
                    y.right.parent = y ;//y的右孩子的父母设为y
            }
            transplant( z , y );//把y移植到z的位置
            y.left = z.left ;
            if(y.left!=null)
                y.left.parent = y ;
            y.color = z.color ;//y的颜色设置为被删除节点z的颜色，这是为了维护红黑树的性质
        }
        if(root == null )
            return null;
        if( y_orginal_color == "black")//如果删除的节点是黑色的，那就可能破坏了红黑是的性质，需要修正
            deleteFixup( x ,x_parent);//修正红黑树的函数
        return z ;
    }
    private void deleteFixup( RBTNode<K,V> x ,RBTNode<K,V> x_parent){
        RBTNode<K,V> w = null ;
        while( root != x && (x==null||x.color == "black")){

            if (x == x_parent.left ) {//如果x是父节点的左孩子
                w = x_parent.right;//w是x的兄弟节点
                if (w != null) {
                    if (w.color == "red") {//case1:x是"黑+黑"节点，x的兄弟节点是红色
                        w.color = "black";//将x的兄弟节点设为黑
                        x_parent.color = "red";
                        leftRotate( x_parent );//对x的父节点进行左旋
                        w = x_parent.right;//左旋后重新设置x的兄弟节点
                    } else if ((w.left == null && w.right == null) || (w.right == null && w.left.color == "black") || (w.left == null && w.right.color == "black") ||
                            (w.left !=null&&w.right!=null&&w.left.color == "black" && w.right.color == "black")) {//case2:x是"黑+黑"节点，
                        // x的兄弟节点是黑，而且兄弟节点的两个孩子节点都是黑色
                        w.color = "red";
                        x = x_parent;
                        x_parent = x.parent ;
                    } else if (w.right == null || w.right.color == "black") {//case2:x是"黑+黑"节点，
                        // x的兄弟节点w是黑色，而且兄弟节点的左孩子是红色，右孩子是黑色的
                        w.left.color = "black";
                        w.color = "red";
                        rightRotate(w);
                        w = x_parent.right;
                    } else {//case 4:case2:x是"黑+黑"节点，
                        // x的兄弟节点w是黑色，而且兄弟节点的右孩子是红色的
                        w.color = x_parent.color;//将父节点的颜色赋给兄弟节点
                        x_parent.color = "black";//将父节点的颜色设置为黑色
                        w.right.color = "black";//将兄弟节点的颜色设为黑色
                        leftRotate(x_parent);//对x的父节点进行左旋
                        x = root;
                    }
                }
            } else {//如果x是x父母的右孩子
                w = x_parent.left;//w是x的兄弟节点
                if (w != null) {
                    if (w.color == "red") {//case1:x是"黑+黑"节点，x的兄弟节点是红色
                        w.color = "black";//将x的兄弟节点设为黑
                        x_parent.color = "red";
                        rightRotate(x_parent);//对x的父节点进行左旋
                        w = x_parent.left;//左旋后重新设置x的兄弟节点
                    } else if ((w.left == null && w.right == null) || (w.right == null && w.left.color == "black") || (w.left == null && w.right.color == "black") ||
                            (w.left !=null&&w.right!=null&&w.left.color == "black" && w.right.color == "black")) {//case2:x是"黑+黑"节点，
                        // x的兄弟节点是黑，而且兄弟节点的两个孩子节点都是黑色
                        w.color = "red";
                        x = x_parent;
                        x_parent = x.parent ;
                    } else if (w.left == null || w.left.color == "black") {//case2:x是"黑+黑"节点，
                        // x的兄弟节点w是黑色，而且兄弟节点的右孩子是红色，左孩子是黑色的
                        w.right.color = "black";
                        w.color = "red";
                        leftRotate(w);
                        w = x_parent.left;
                    } else {//case 4:case2:x是"黑+黑"节点，
                        // x的兄弟节点w是黑色，而且兄弟节点的左孩子是红色的
                        w.color = x_parent.color;//将父节点的颜色赋给兄弟节点
                        x_parent.color = "black";//将父节点的颜色设置为黑色
                        w.left.color = "black";//将兄弟节点的颜色设为黑色
                        rightRotate(x_parent);//对x的父节点进行右旋
                        x = root;
                    }
                }
            }
        }
        x.color = "black";
    }
    private void leftRotate(RBTNode< K, V > x){//左旋
        RBTNode<K,V>  y = x.right;//y表示x的右孩子
        x.right = y.left ;//y的左孩子设为x的右孩子
        if(y.left !=null )
            y.left.parent = x ;//y的左孩子的父母设置为x
        y.parent = x.parent ;//y的父母设置为x的父母
        if(x.parent == null ){
            root = y ;
        }else if( x == x.parent.left ){//x是x父母的左孩子
            x.parent.left = y ;
        }else {//x是x父母的右孩子
            x.parent.right = y ;
        }
        x.parent = y ;
        y.left = x ;
    }
    private void rightRotate( RBTNode<K , V>  y ){//右旋
        RBTNode<K,V> x = y.left ;//x表示y的左孩子
        y.left = x.right ;//x的右孩子设为y的左孩子
        if(x.right!=null)
           x.right.parent = y;//x的右孩子的父母设为y
        x.parent = y.parent;//x的父母设为y的父母
        if( y.parent == null ){//y是根
            root = x ;
        }else if( y == y.parent.left ){//y是y父母的左孩子
            y.parent.left = x ;
        }else{//y是y父母的右孩子
            y.parent.right = x ;
        }
        x.right = y ;//x的左孩子设为y
        y.parent = x ;//y的父母设为x
    }
    public void insert( K key , V v ){
        RBTNode rbtNode = new RBTNode(key , v);
        insert( rbtNode );
    }
    public void insert( RBTNode<K,V> z ){
        RBTNode<K,V> rbtNode = new RBTNode<K,V>();
        RBTNode<K,V> x = root ,y = null;
        while( x !=null ){
                y = x ;
                if( comparator.compare(x.key , z.key) > 0){
                    x = x.left ;
                }else{
                    x = x.right;
                }
        }
        z.parent = y ;
        if( y == null )
            root = z ;
        else if( comparator.compare(z.key ,y.key) > 0)//如果z节点的key大于y的key
            y.right = z ;//y的右孩子设为x
        else
            y.left = z ;//y的左孩子设为y
        z.color = "red";
        rbInsertFixup( z );
    }
    private void rbInsertFixup( RBTNode<K,V>  z ){//  通过这个方法对红黑树的节点进行颜色的修改以及旋转，让树T依然是一颗红黑树
        /*case1:如果插入的节点父节点的颜色是黑色，这样不会破坏红黑树的性质，所以无需做任何调整*/
        while(z.parent!=null&&z.parent.color.equals("red")){//case3:如果被插入节点的父母节点是红色
            if( z.parent.parent!=null&& z.parent == z.parent.parent.left ){//如果z的父母节点是z的祖父母的左孩子
                RBTNode<K,V> y = z.parent.parent.right;//y是z的叔叔节点
                    if (y!=null&&y.color.equals("red")) {//case3-1:如果叔叔节点是红色的
                        z.parent.color = "black";//z的双亲节点变成黑色
                        y.color = "black";//叔叔节点变成黑色
                        z.parent.parent.color = "red";//祖父节点颜色变成红色
                        z = z.parent.parent;//将当前节点z置为z的祖父指点,在迭代中判断z是否破坏
                    } else if ( z == z.parent.right) {//case3-2:叔叔节点是黑色，而且z是z父母节点的右孩子
                        z = z.parent ;//将z的父母节点赋给z
                        leftRotate( z );//左旋
                    } else {//case3-3:叔叔是黑色，z是z父母节点的左孩子
                        z.parent.color = "black";//将z的父母节点赋值为黑色
                        z.parent.parent.color="red";//将z的祖父节点的颜色变为红色
                        rightRotate( z.parent.parent );//对z的祖父节点进行右旋
                    }
            }else{//z的父母节点是祖父母的节点的右孩子，所以这里的操作只需要将上面的左右对调就行了
                RBTNode<K,V> y = z.parent.parent.left;//y是z的叔叔节点
                    if (y!=null&&y.color.equals("red")) {//case3-1:如果叔叔节点是红色的
                        z.parent.color = "black";//z的双亲节点变成黑色
                        y.color = "black";//叔叔节点变成黑色
                        z.parent.parent.color = "red";//祖父节点颜色变成红色
                        z = z.parent.parent;//将当前节点z置为z的祖父指点,在迭代中判断z是否破坏
                    } else if ( z == z.parent.left) {//case3-2:叔叔节点是黑色，而且z是z父母节点的左孩子
                        z = z.parent ;//将z的父母节点赋给z
                        rightRotate( z );//左旋
                    } else {//case3-3:叔叔是黑色，z是z父母节点的右孩子
                        z.parent.color = "black";//将z的父母节点赋值为黑色
                        z.parent.parent.color="red";//将z的祖父节点的颜色变为红色
                        leftRotate( z.parent.parent );//对z的祖父节点进行左旋
                    }
            }
        }
        root.color="black";//把树根修改成黑色(case2:如果插入的节点是树根，那么可以直接修改树根节点)
    }

    class RBTNode<K,V>{
        private K key;
        private V value;
        private String color;
        private RBTNode<K,V> left;
        private RBTNode<K,V> right;
        private RBTNode<K,V> parent;

        public RBTNode<K, V> getLeft() {
            return left;
        }

        public void setLeft(RBTNode< K, V> left) {
            this.left = left;
        }

        public RBTNode<K, V> getRight() {
            return right;
        }
        public RBTNode(K key , V value ){
            this();
            this.key = key ;
            this.value = value ;
        }

        public void setRight(RBTNode<K, V> right) {
            this.right = right;
        }

        public RBTNode<K, V> getParent() {
            return parent;
        }

        public void setParent(RBTNode<K, V> parent) {
            this.parent = parent;
        }

        public RBTNode(K key, V value , RBTNode<K, V> parent) {
            this();
            this.key = key;
            this.value = value;
            this.parent = parent;
        }
        public RBTNode(){
            left = null;
            right = null;
            parent = null;
            this.color = "red" ;
        }
        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public RBTNode(K key, V value, String color, RBTNode<K, V> left, RBTNode<K, V> right, RBTNode<K, V> parent) {
            this.key = key;
            this.value = value;
            this.color = color;
            this.left = left;
            this.right = right;
            this.parent = parent;
        }
    }
    public void printRBT(){
        if(root == null) {
            System.out.println("该树是空树");
            return ;
        }
        System.out.print("先序遍历:");
        preOrder(root);
        System.out.println();
        System.out.print("中序遍历:");
        inOrder(root);
        System.out.println();
        System.out.print("后序遍历:");
        postOrder(root);
        System.out.println();


    }
    private void preOrder(RBTNode<K,V> r){
        if( r != null){
            System.out.print(r.key+"("+r.color+")");
            preOrder(r.left);
            preOrder(r.right);
        }
    }
    private void transplant( RBTNode<K ,V> u, RBTNode<K,V> v){//移植,把v移动到u的位置
        if ( u.parent == null )
            root = v ;
        else if ( u == u.parent.left )
            u.parent.left = v ;
        else
            u.parent.right = v ;
        if( v != null)//保证v不为空
           v.parent = u.parent ;
    }
    private void inOrder(RBTNode<K,V> r){
        if( r != null){
            inOrder(r.left);
            System.out.print(r.key+"("+r.color+")");
            inOrder(r.right);
        }
    }
    private RBTNode<K,V> getNode( K k ){//寻找与key 相应的node节点
        RBTNode<K,V> x = root ;
        while( x != null ){
            if( comparator.compare(x.key , k )> 0){
                x = x.left ;
            }else if(comparator.compare(x.key , k)<0){
                x = x.right ;
            }else
                return x ;
        }
        return null ;
    }
    public void deleteNode(K key ){//删除k为value的节点
        RBTNode<K,V> rbtNode = this.getNode( key ) ;
        if( rbtNode == null ){
            return ;
        }
        this.deleteNode(rbtNode) ;
    }

    private void postOrder(RBTNode<K,V> r){
        if( r != null){
            postOrder(r.left);
            postOrder(r.right);
            System.out.print(r.key+"("+r.color+")");
        }
    }

    public static void main(String args[]){
        RedBlackTree<Integer,Integer> rbt = new RedBlackTree<Integer, Integer>();
        rbt.setComparator(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2) ;
            }
        });
        int a[] = new int[8] ;
        for( int i = 0 ; i < 8 ;i ++ ){
            a[i] = (int)(Math.random()*1001);
        }
        System.out.println(Arrays.toString(a)) ;
        for( int i : a){
            rbt.insert(i , null);
        }
        rbt.printRBT();
        System.out.println("\n测试删除:");
        for( int i = 0 ; i < 8 ;i ++){
            System.out.println("删除节点 "+a[i]);
            rbt.deleteNode(a[i]);
            System.out.println("结果:");
            rbt.printRBT();
            System.out.println();
        }

    }
}
/*
* [53, 36, 1, 61, 5, 95, 33, 55]*/