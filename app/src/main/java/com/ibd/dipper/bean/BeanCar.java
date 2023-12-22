package com.ibd.dipper.bean;



import java.io.Serializable;
import java.util.List;

public class BeanCar implements Serializable {
    public String fileNumber;
    public String approvedLoad;
    public String energyTypeText;
    public String transportCertificate;
    public String loads;
    public String length;
    public String type;
    public int certificationStatus;
    public String totalMass;
    public String licensePlateColor;
    public String licensePlateColorText;
    public String licensePlateTypeText;
    public String energyType;
    public String trailerLicensePlate;
    public String trailerLoads;
    public String trailerLength;
    public String trailerWidth;
    public String trailerHeight;
    public String trailerTotalMass;
    public String trailerLicenseFront;
    public String trailerLicenseBack;

    public String licensePlateNumber;
    public String licenseFront;
    public String typeText;
    public String transportCertificateNumber;
    public String width;
    public String certificationStatusText;
    public int id;
    public String licenseMain;
    public String licensePlateType;
    public String height;
    public String certificationReason;

    public boolean Choose;

    public List<BeanCarTrailer> trailerList;
}
