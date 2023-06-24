package com.driver;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Repository
@NoArgsConstructor
@AllArgsConstructor
public class OrderRepository {

    HashMap<String,Order> orderMap = new HashMap<>();
    HashMap<String,DeliveryPartner> partnerMap = new HashMap<>();
    HashMap<DeliveryPartner, List<Order>> orderPartnerPairHashMap = new HashMap<>();
    HashSet<Order> assigned = new HashSet<>();
    public void addOrder(Order order){
        orderMap.put(order.getId(),order);
    }
    public void addDeliveryPartner(DeliveryPartner deliveryPartner){
        partnerMap.put(deliveryPartner.getId(),deliveryPartner);
    }
    public void addOrderPartnerPair(String orderId,String partnerId){
        if (partnerMap.containsKey(partnerId) && orderMap.containsKey(orderId)){
            List<Order> orders = new ArrayList<>();
            if (orderPartnerPairHashMap.containsKey(partnerMap.get(partnerId))){
                orders = orderPartnerPairHashMap.get(partnerMap.get(partnerId));
            }
            orders.add(orderMap.get(orderId));
            orderPartnerPairHashMap.put(partnerMap.get(partnerId),orders);
            assigned.add(orderMap.get(orderId));
            partnerMap.get(partnerId).setNumberOfOrders(orders.size());
        }
    }
    public Order getOrderById(String orderId){
        return orderMap.get(orderId);
    }
    public DeliveryPartner getPartnerById(String partnerId){
        return partnerMap.get(partnerId);
    }
    public Integer getOrderCountByPartnerId(String partnerId){
        Integer count = 0;
        if (orderPartnerPairHashMap.containsKey(partnerMap.get(partnerId))){
            count = orderPartnerPairHashMap.get(partnerMap.get(partnerId)).size();
        }
        return count;
    }
    public List<String> getOrdersByPartnerId(String partnerId){
        List<String> orders = new ArrayList<>();
        if (partnerMap.containsKey(partnerId)) {
            if (orderPartnerPairHashMap.containsKey(partnerMap.get(partnerId))) {
                List<Order> orderList = orderPartnerPairHashMap.get(partnerMap.get(partnerId));
                for (Order order : orderList){
                    orders.add(order.getId());
                }
            }
        }
        return orders;
    }
    public List<String> getAllOrders(){
        List<String> orders = new ArrayList<>();
        for (String orderId : orderMap.keySet()){
            orders.add(orderId);
        }
        return orders;
    }
    public Integer getCountOfUnassignedOrders(){
        return orderMap.size()-assigned.size();
    }
    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time,String partnerId){
        Integer count = 0;
        String[] str = time.split(":");
        Integer currTime = Integer.parseInt(str[0])*60 + Integer.parseInt(str[1]);
        if (orderPartnerPairHashMap.containsKey(partnerMap.get(partnerId))){
            List<Order> orders = orderPartnerPairHashMap.get(partnerMap.get(partnerId));
            for (Order order : orders){
                if (orderMap.containsKey(order.getId())){
                    Order currOrder = orderMap.get(order.getId());
                    if (currTime < currOrder.getDeliveryTime()){
                        count++;
                    }
                }
            }
        }
        return count;
    }
    public String getLastDeliveryTimeByPartnerId(String partnerId){
        Integer time = 0;
        List<Order> orders = orderPartnerPairHashMap.get(partnerMap.get(partnerId));
        for (Order order : orders){
            time = Math.max(time,order.getDeliveryTime());
        }
        Integer minutes = time%60;
        Integer hours = time/60;
        String lastTime = "";
        if (hours < 10){
            lastTime = "0"+ hours;
        }
        else{
            lastTime += hours;
        }
        lastTime += ":";
        if (minutes == 0 || minutes < 10){
            lastTime += "0" + minutes;
        }
        else{
            lastTime += minutes;
        }
        return lastTime;
    }
    public void deletePartnerById(String partnerId){
        if (orderPartnerPairHashMap.containsKey(partnerMap.get(partnerId))){
            List<Order> orders = orderPartnerPairHashMap.get(partnerMap.get(partnerId));
            for (Order order : orders){
                assigned.remove(order);
            }
            orderPartnerPairHashMap.remove(partnerMap.get(partnerId));
            partnerMap.remove(partnerId);
        }
    }
    public void deleteOrderById(String orderId) {
        if (orderMap.containsKey(orderId)){
            if (assigned.contains(orderMap.get(orderId))){
                assigned.remove(orderMap.get(orderId));
            }
            for (List<Order> orderList : orderPartnerPairHashMap.values()){
                for (Order order : orderList){
                    if (order.equals(orderMap.get(orderId))){
                        orderList.remove(order);
                        orderMap.remove(orderId);
                        return;
                    }
                }
            }
            orderMap.remove(orderId);
        }
    }
}