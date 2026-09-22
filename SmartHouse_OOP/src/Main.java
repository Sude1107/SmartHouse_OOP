import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

//    Methods (functions)
    static String deviceId() {
        Scanner userInput = new Scanner(System.in);
        System.out.println("Enter Device ID: ");
        return userInput.nextLine();
    }

    static void usedAddDevice(SmartHouse house, SmartDevice device) {
        try {
            house.addDevice(device);
        }catch (DuplicateDeviceDetector duplicate) {
            System.out.println("Error Message: " + duplicate.getMessage());
        }
    }

    static SmartDevice createDevice() {
        Scanner userInput = new Scanner(System.in);
        System.out.println("Please choose the device type: (light,thermostat,door) ");
        String type = userInput.nextLine();
        System.out.println("Device Id: ");
        String id = userInput.nextLine();
        System.out.println("Device Name: ");
        String name = userInput.nextLine();
        if(type.equalsIgnoreCase("light")) {
            System.out.println("Power Status (On/Off): ");
            String status = userInput.nextLine();
            boolean powerStatus = false;
            if(status.equalsIgnoreCase("On")) {
                powerStatus = true;
            }
            System.out.println("Brightness Value: ");
            int brightnessValue = userInput.nextInt();
            System.out.println("Color: ");
            String color = userInput.nextLine();
            return new SmartLight(id, name, powerStatus, brightnessValue, color);
        }
        else if(type.equalsIgnoreCase("thermostat")) {
            System.out.println("Power Status (On/Off): ");
            String status = userInput.nextLine();
            boolean powerStatus = false;
            if(status.equalsIgnoreCase("On")) {
                powerStatus = true;
            }
            System.out.println("Target Temperature (Celsius): ");
            double targetTemperature = userInput.nextDouble();
            System.out.println("Current Temperature (Celsius): ");
            double currentTemperature = userInput.nextDouble();

            return new SmartThermostat(id, name, powerStatus, targetTemperature, currentTemperature);
        }
        else if(type.equalsIgnoreCase("door")) {
            System.out.println("Is Locked (Y/N): ");
            boolean locked = false;
            try {
                if (userInput.nextLine().equalsIgnoreCase("Y")) {
                    locked = true;
                }else if  (userInput.nextLine().equalsIgnoreCase("N")) {
                    locked = false;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid Input");
                createDevice();
            }
            System.out.println("Pin Code:");
            int pinCode = userInput.nextInt();
            return new SmartDoor(id, name, locked, pinCode);

        }
        return null;
    }
    static void userChoice(SmartHouse house, int choice) {
        if (choice == 1) {
            house.showDevices();
        }
        else if (choice == 2) {
            SmartDevice newDevice = createDevice();
            usedAddDevice(house, newDevice);
        }
        else if (choice == 3) {
            house.turnOnDevice(deviceId());
        }
        else if (choice == 4) {
            house.turnOffDevice(deviceId());
        }
        else if (choice == 5) {
            house.primaryAction(deviceId());
        }
//        ***********
        else if (choice == 6) {
            SmartDevice d = house.findDevice(deviceId());
            try {
                System.out.println(d.getEnergyUsage());
            }catch (Exception e) {
                System.out.println("Error Message: " + e.getMessage());
            }
        }

    }



    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        SmartHouse house = new SmartHouse();

        SmartDevice light = new SmartLight("L651", "Room1light", false, 0, "cold");
        SmartDevice door2 = new SmartDoor("D261", "room1Door", false, 1903);
        SmartDevice thermostat = new SmartThermostat("T511", "Room1Thermo", true, 10.5, 20.0);
        SmartDevice door = new SmartDoor("D261", "room1Door", false, 1903);

        boolean activeProgram = true;
        while (activeProgram) {
            System.out.println("========== SMART HOME ==========\n" +
                    "\n" +
                    "1. Show devices\n" +
                    "2. Add device\n" +
                    "3. Turn device ON\n" +
                    "4. Turn device OFF\n" +
                    "5. Perform primary action\n" +
                    "6. Show energy usage\n" +
                    "0. Exit\n" +
                    "\n" +
                    "Choose:");
            int user = input.nextInt();
            if (user == 0) {
                activeProgram = false;
            }
            userChoice(house, user);

        }

    }
}


//  CLASSES*********************
// EXCEPTIONS
class DuplicateDeviceDetector extends Exception {
    public DuplicateDeviceDetector() {}
    public DuplicateDeviceDetector(String message) {
        super(message);
    }
}

//INTERFACE
interface WifiConnectable {
    public void connectWifi(String networkName);
    public String getIp();
}

// HOUSE
class SmartHouse {
    private ArrayList<SmartDevice> allDevices;

    public SmartHouse() {
        this.allDevices = new ArrayList<SmartDevice>();
    }

    public void addDevice(SmartDevice device) throws DuplicateDeviceDetector {
        for (SmartDevice d : allDevices) {
            if (d.getDeviceId().equals(device.getDeviceId())) {
                throw new DuplicateDeviceDetector("Device id is already exists!");
            }
        }
        this.allDevices.add(device);
        System.out.println("Device " + device.getDeviceId() + " added successfully!");

    }

    public void showDevices() {
        for (SmartDevice d : allDevices) {
            System.out.println(d.toString());
        }
    }

    public void allActions() {
        for (SmartDevice d : allDevices) {
            d.primaryAction();
        }
    }

    public void primaryAction(String id) {
        try {
            findDevice(id).primaryAction();
        }catch (Exception e) {
            System.out.println("Error Message: " + e.getMessage());
            System.out.println("No such Device!");
        }
    }

    public SmartDevice findDevice(String id) {
        for (SmartDevice d : allDevices) {
            if (d.getDeviceId().equals(id)) {
                System.out.println("Device found!\n" + d.getDeviceName());
                return d;
            }
        }
        System.out.println("Device not found!");
        return null;
    }
    public void turnOnDevice(String id) {
        SmartDevice device = findDevice(id);

        if  (device != null) {
            device.turnOn();
        }
        else {
            System.out.println("Device not found!");
        }
    }
    public void turnOffDevice(String id) {
        SmartDevice device = findDevice(id);

        if (device != null) {
            device.turnOff();
        }
        else {
            System.out.println("Device not found!");
        }
    }
    public void getTotalEnergyUsage() {
        double totalEnergyUsage = 0.0;
        for (SmartDevice d : allDevices) {
            totalEnergyUsage += d.getEnergyUsage();
        }
        System.out.print("Total energy usage: " + totalEnergyUsage + "kWh");
    }

}

abstract class SmartDevice{
    private String deviceId;
    private String deviceName;
    private boolean isPoweredOn;
//    private ArrayList<SmartDevice> devices = new ArrayList<SmartDevice>();

    public SmartDevice(String deviceId, String deviceName) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
    }
    public SmartDevice(String deviceId, String deviceName, boolean isPoweredOn) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.isPoweredOn = isPoweredOn;
    }

    public abstract void primaryAction();
    public abstract double getEnergyUsage();

    public void turnOn() {
        if (!isPoweredOn){
            this.isPoweredOn = true;
            System.out.println("Turning on");
        }
        else {
            System.out.println("Device is already on");
        }
    }
    public void turnOff() {
        if (isPoweredOn){
            this.isPoweredOn = false;
            System.out.println("Turning off");
        }
        else {
            System.out.println("Device is already off");
        }
    }

    //    setters
    public void setDeviceId(String id) {
        this.deviceId = id;
    }
    public void setDeviceName(String name) {
        this.deviceName = name;
    }
    public void setPowerStatus(boolean isPower) {
        this.isPoweredOn = isPower;
    }

    //    getters
    public String getDeviceId() {
        return this.deviceId;
    }
    public String getDeviceName() {
        return this.deviceName;
    }
    public boolean getPowerStatus() {
        return this.isPoweredOn;
    }

    //    to String
    public String toString(){
        return "Device Name: " + getDeviceName();
    }

}

// subclasses

class SmartLight extends SmartDevice implements WifiConnectable{
    private int brightnessLevel;
    private String color;
    private String ipAddress;

    public SmartLight(String deviceId, String deviceName, boolean powerStatus, int brightnessLevel, String color) {
        super(deviceId, deviceName, powerStatus);

        if (brightnessLevel >= 0 && brightnessLevel <= 100) {
            this.brightnessLevel = brightnessLevel;
        }
        else {
            this.brightnessLevel = 0;
        }
        this.color = color;
        this.ipAddress = "0.0.0.0";
    }

    @Override
    public void primaryAction() {
        System.out.println(getDeviceName() + " Illuminating room at " + this.brightnessLevel + "% brightness in a " + this.color + " color");
    }

    @Override
    public double getEnergyUsage() {
        return this.brightnessLevel * 0.15;
    }

    @Override
    public void connectWifi(String networkName) {
        this.ipAddress = "192.168.1." + (int)(Math.random() * 100);
        System.out.println("Connecting to " + networkName + "with IP: " + this.ipAddress);
    }

    @Override
    public String getIp() {
        return this.ipAddress;
    }

//    setters
    public void setBrightness(int brightnessLevel) {
        this.brightnessLevel = brightnessLevel;
    }
    public void setColor(String color) {
        this.color = color;
    }

//    getters
    public int getBrightness() {
        return this.brightnessLevel;
    }
    public String getColor() {
        return this.color;
    }


}

class SmartThermostat extends SmartDevice implements WifiConnectable{
    private double targetTemp;
    private double currentTemp;
    private String ipAddress;

    public SmartThermostat(String deviceId, String deviceName, boolean powerStatus, double targetTemp, double currentTemp) {
        super(deviceId, deviceName, powerStatus);
        this.targetTemp = targetTemp;
        this.currentTemp = currentTemp;
    }

    @Override
    public void primaryAction() {
        if (this.targetTemp > this.currentTemp) {
            System.out.println(getDeviceName() + " is heating!");
        }
        else if (this.targetTemp < this.currentTemp) {
            System.out.println(getDeviceName() + " is cooling!");
        }
        else{
            System.out.println(getDeviceName() + "is sleeping!");
        }
    }

    @Override
    public double getEnergyUsage() {
        double usg = this.targetTemp - this.currentTemp;
        if (usg < 0) {
            usg = usg * (-1);
        }
        return usg * 10;
    }

    @Override
    public void connectWifi(String networkName) {
        this.ipAddress = "192.168.1." + (int)(Math.random() * 253);
        System.out.println("Connecting to " + networkName + "with IP: " + this.ipAddress);
    }

    @Override
    public String getIp() {
        return this.ipAddress;
    }

//    setters
    public void setTargetTemp(double targetTemp) {
        this.targetTemp = targetTemp;
    }
    public void setCurrentTemp(double currentTemp) {
        this.currentTemp = currentTemp;
    }

//    getters
    public double getTargetTemp() {
        return this.targetTemp;
    }
    public double getCurrentTemp() {
        return this.currentTemp;
    }
}

class SmartDoor extends SmartDevice {
    private boolean isLocked;
    private int pinCode;

    public SmartDoor(String deviceId, String deviceName, boolean isLocked, int pinCode) {
        super(deviceId, deviceName);
        this.isLocked = isLocked;
        this.pinCode = pinCode;
    }

    @Override
    public void primaryAction() {
        String status;
        if (isLocked) {
            status = "LOCKED";
        }else{
            status = "UNLOCKED";
        }
        System.out.println(getDeviceName() + " Door Locked Status: " + status);
    }
    @Override
    public double getEnergyUsage() {
        return 2.6;
    }

//    setters
    public void setPinCode(int pinCode) {
        this.pinCode = pinCode;
    }
    public void setLockStatus(boolean isLocked) {
        this.isLocked = isLocked;
    }


//    getters
    public int getPinCode() {
        return this.pinCode;
    }
    public boolean getLockedStatus() {
        return this.isLocked;
    }
}




