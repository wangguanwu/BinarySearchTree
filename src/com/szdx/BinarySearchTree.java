package com.szdx;

import java.util.*;

class Node<K,V>{
    private K key ;
    private V value;
    private Node parent;
    private Node leftChild ;
    private Node rightChild;
    public Node(K key , V value ){
        this();
        this.key = key ;
        this.value = value ;
    }
    public Node(){
        this.parent = null;
        this.leftChild = null;
        this.rightChild = null ;
    }

    public K getKey() {
        return key;
    }

    public Node<K,V> getParent() {
        return parent;
    }

    public void setParent(Node<K,V> parent) {
        this.parent = parent;
    }

    public Node getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(Node leftChild) {
        this.leftChild = leftChild;
    }

    public Node<K,V> getRightChild() {
        return rightChild;
    }

    public void setRightChild(Node rightChild) {
        this.rightChild = rightChild;
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
}
public class BinarySearchTree<K,V>{
    private Comparator<K> comparator =null;
    private Node root = null;
    public  BinarySearchTree(Map<K , V> values){
        this();
        this.setValues(values);
    }
    public void setValues(Map<K,V> values){
        Set<Map.Entry<K,V>> entry = values.entrySet();
        Iterator<Map.Entry<K,V>> it = entry.iterator();
        while( it.hasNext() ){
            Map.Entry<K,V> item = it.next();
            Node<K,V> node = new Node<>(item.getKey() , item.getValue());
            this.insertNode(node);
        }
    }
    public BinarySearchTree(){
    }
    public void insertNode(Node<K,V> items) {
       Node<K,V> y = null ;
       Node<K ,V> x = this.root ;
       K key = items.getKey();
       while( x != null ){
           y = x ;
           K k = x.getKey();
           if(comparator.compare(key , k) > 0 )
               x = x.getRightChild();
           else
               x = x.getLeftChild();
       }
       items.setParent(y);
       if(  y == null ){
           root = items ;
       }else if ( comparator.compare(key , y.getKey()) < 0){
           y.setLeftChild(items);
       }else{
           y.setRightChild(items);
       }
    }
    public Node<K,V> treeSearch(Node<K , V> root , K key){
        while(  root!=null && key.equals(root.getKey())  ){
            if( comparator.compare(key , root.getKey()) > 0)
                root = root.getLeftChild();
            else
                root = root.getRightChild();
        }
        return root ;
    }
    public Node<K,V> treeMinimum(Node<K,V> m ){//最小的节点
        while(m.getLeftChild()!=null){
            m = m.getLeftChild();
        }
        return m ;
    }
    public void setComparator(Comparator<K> comparator ){
        this.comparator = comparator ;
    }
    public Node<K,V> treeMaxmum(Node<K,V> m ){//最大的节点
        while(m.getRightChild()!=null){
            m = m.getRightChild();
        }
        return m ;
    }
    public Node<K,V> treeSucessor(Node<K,V> s){//得到后继
        if( s.getRightChild() != null )
            return treeMinimum(s.getRightChild());
        Node<K,V> y  = s.getParent();
        while( y != null && s == y.getRightChild()){
            s = y ;
            y = y.getParent();
        }
        return y ;
    }

    public boolean deleteNode(K key){
        Node items = this.treeSearch( root ,key);
        return deleteNode(items);
    }
    private void transplant( Node<K ,V> u , Node<K ,V> v ){//移植节点，把v移到u的位置
        if( u.getParent() == null ){
            root = u ;
        }else if( u == u.getParent().getLeftChild()){
            u.getParent().setLeftChild(v);
        }else{
            u.getParent().setRightChild(v);
        }
        if( v != null ){
            u.setParent(v.getParent());
        }
    }
    public boolean deleteNode(Node<K , V> items ){
        if( items.getLeftChild() == null ){//左孩子为空的情况
            transplant( items , items.getRightChild());
        }else if( items.getRightChild() == null ){//右孩子为空的情况
            transplant( items , items.getLeftChild());
        }else{//存在左右孩子的情况
            Node<K , V> sucessor = this.treeSucessor( items.getRightChild() );
            if( sucessor.getParent()!= items ){//如果items的后继sucessor不是items的子节点
                transplant( sucessor , sucessor.getRightChild()); //把sucessor的右孩子移到sucessor的位置
                sucessor.setRightChild(items.getRightChild());//sucessor的右孩子指向items的右孩子
                sucessor.getRightChild().setParent(sucessor);//items的右孩子的父母节点变为sucessor
            }
            transplant( items , sucessor );
            sucessor.setLeftChild(items.getLeftChild());//把sucessor的左孩子指向items的左孩子
            items.getLeftChild().setParent(sucessor);//把items的父母节点变为sucessor
        }
        return false;
    }
    private void inOrder(Node<K ,V> r ){
        if( r == null )
            return ;
        inOrder(r.getLeftChild());
        System.out.println(r.getKey());
        inOrder(r.getRightChild());

    }
    public void inOrder(){
        inOrder(root);
    }
    public static void main(String args[]){
        BinarySearchTree<Integer,Integer> bs = new BinarySearchTree<>();
        Map<Integer,Integer> map = new HashMap<>();
        for( int i = 0 ; i < 10 ; i++ ){
            map.put((int)(Math.random()*1000+1),null);
        }
        bs.setComparator(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });
        bs.setValues(map);
        bs.inOrder();
    }

}
