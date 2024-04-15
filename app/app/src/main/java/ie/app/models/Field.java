package ie.app.models;

import static java.lang.Math.*;

import android.util.Log;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ie.app.api.FirebaseAPI;

public class Field {

    final public int numberOfSoilLayer = 5;
    final double depth = 0.9; // soil depth in m
    final double layerThickness = depth / numberOfSoilLayer; // thickness of a layer in m
    final private double areaPerPlant = 1.00 * 1.00;
    final private double thetaS = 0.27; // Saturated water capacity
    final private double thetaR = 0.015; // Remaining water capacity
    final private double thetaG = 0.02; // Lượng nước sai lệch do trọng lực
    double autoIrrigationTime = -1;
    boolean zerodrain = true;

    public String name;
    public MeasuredData measuredData = new MeasuredData();
    public CustomizedParameter customizedParameter = new CustomizedParameter();
    public IrrigationInformation irrigationInformation = new IrrigationInformation();
    public TreeData treeData = new TreeData();
    public List<List<Double>> allMeasuredData = new ArrayList<>();

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + measuredData.toString() + "\n" + customizedParameter.toString();
    }

//    ----------------------------------All simulation--------------------------

    public String simulation() {
        List<Double> _treeData = new ArrayList<>();
        _treeData.add(treeData.LDM);//                      0
        _treeData.add(treeData.SDM);//                      1
        _treeData.add(treeData.RDM);//                      2
        _treeData.add(treeData.SRDM);//                     3
        _treeData.add(treeData.LA);//                       4

        _treeData.add(treeData.mDMl);//                     5
        _treeData.add(treeData.Clab);//                     6

        _treeData.addAll(treeData.rootLength);//            7 -> 11
        _treeData.addAll(treeData.rootTips);//              12 -> 16
        _treeData.addAll(treeData.soilWaterCapacity);//     17 -> 21
        _treeData.addAll(treeData.contL);//                 22 -> 26
        _treeData.addAll(treeData.nuptL);//                 27 -> 31

        LocalTime x;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            x = LocalTime.parse(irrigationInformation.getDuration());
            _treeData.add(x.getHour()/24.0 + x.getMinute()/24.0/60.0 + x.getSecond()/24.0/60.0/60.0); // 32
        }

        for (int i = 0; i < allMeasuredData.size(); i++) {
            List<Double> weatherData = new ArrayList<>();
            for (int j = 1; j < 5; j++) {
                weatherData.add(allMeasuredData.get(i).get(j));
            }
            double t = 5.0 / 24 / 60;
            rk4Step(allMeasuredData.get(i).get(0) - treeData.growTime, _treeData, t, weatherData);
        }

        FirebaseAPI.changeTreeData("users", getName(), _treeData);
        if (_treeData.get(32) == 0) {
            return "00:00:00";
        } else {
            double duration = _treeData.get(32) / customizedParameter.dripRate;
            while (duration < 0.01) {
                duration *= 10;
            }
            int hour = (int) (duration * 24.0);
            int minute = (int) (duration * 24.0 * 60.0 - hour * 60.0);
            int second = (int) (duration * 24.0 * 60.0 * 60.0 - minute * 60.0) % 60;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                String dur = LocalTime.of(hour, minute, second).toString();
                Log.e("dur", dur);
                return dur;
            }
        }
        return "00:00:00";
    }

    // Ngưỡng hàm lượng nước duy trì
    private double fieldCapacityThreshHold() {
        int length = customizedParameter.fieldCapacity.size();
        double threshHold = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String x = LocalDate.now().toString();
            for (int i = 0; i < length; i++) {
                if (x.compareTo(customizedParameter.fieldCapacity.get(i).startTime) > 0 &&
                        x.compareTo(customizedParameter.fieldCapacity.get(i).endTime) < 0) {
                    threshHold = customizedParameter.fieldCapacity.get(i).getThreshHold();
                }
            }
        }
        return threshHold * (thetaS - thetaR) / 100 + thetaR;
    }

    double monod(double conc, double Imax) {
        double pc = max(0.0, conc);
        return (pc * Imax / (6.0 + pc));
    }

    double logistic(double ct, double x0, double xc, double k, double m) {
        return (x0 + (m - x0) / (1 + exp(-k * (ct - xc))));
    }

    private double fSLA(double ct) {
        return (logistic(ct, 0.04, 60, 0.1, 0.0264));
    }

    private double fKroot(double th, double rl) {
        final double Ksr = 0.01; // Độ dẫn thuỷ lực gữa đất và bề mặt rễ
        double rth = relTheta(th);
        double kadj = min(1.0, Math.pow(rth/0.4, 1.5));
        return kadj * Ksr * rl;
    }

    private double relTheta(double th) {
        return lim((th - thetaR) / (thetaS - thetaR));
    }

    private double lim(double x) {
        if (x > 1) {
            return (1);
        } else if (x < 0) {
            return (0);
        } else {
            return x;
        }
    }

    private List<Double> multiplyLists(List<Double> l1, List<Double> l2) {
        int n = min(l1.size(), l2.size());
        List<Double> x = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            x.add(l1.get(i) * l2.get(i));
        }
        return x;
    }

    private double hourlyET(
            double tempC,
            double radiation,
            double relativeHumidity,
            double wind,
            double doy,
            double latitude,
            double longitude,
            double elevation,
            double longZ,
            double height){
        double hours = (doy % 1) * 24;
        double tempK = tempC + 273.16;

        double Rs = radiation * 3600 / 1e+06; // radiation mean Ra
        double P = 101.3 *
                pow((293 - 0.0065 * elevation) / 293,
                        5.256); // cong thuc tinh ap suat khong khi o do cao elevation


        double psi = 0.000665 * P; // cong thuc tinh gamma

        double Delta = 2503 *
                exp((17.27 * tempC) / (tempC + 237.3)) /
                (pow(tempC + 237.3, 2)); // cong thuc tinh delta- cai hinh tam giac
        double eaSat = 0.61078 *
                exp((17.269 * tempC) /
                        (tempC +
                                237.3)); // cong thuc tinh ap suat hoi bao hoa tai nhiet do K
        double ea = (relativeHumidity / 100) * eaSat;

        double DPV = eaSat - ea;
        double pi = 3.14;
        double dr = 1 + 0.033 * (cos(2 * pi * doy / 365.0)); //dr
        double delta = 0.409 *
                sin((2 * pi * doy / 365.0) -
                        1.39); // cong thuc tinh delta nhung cai hinh cai moc
        double phi = latitude * (pi / 180);
        double b = 2.0 * pi * (doy - 81.0) / 364.0;

        double Sc = 0.1645 * sin(2 * b) - 0.1255 * cos(b) - 0.025 * sin(b);
        double hourAngle = (pi / 12) *
                ((hours +
                        0.06667 * (longitude * pi / 180.0 - longZ * pi / 180.0) +
                        Sc) -
                        12.0); // w
        double w1 = hourAngle - ((pi) / 24);
        double w2 = hourAngle + ((pi) / 24);
        double hourAngleS = acos(-tan(phi) * tan(delta)); // Ws
        double w1c = (w1 < -hourAngleS)
                ? -hourAngleS
                : (w1 > hourAngleS)
                ? hourAngleS
                : Math.min(w1, w2);
        double w2c = (w2 < -hourAngleS)
                ? -hourAngleS
                : Math.min(w2, hourAngleS);

        double Beta =
                asin((sin(phi) * sin(delta) + cos(phi) * cos(delta) * cos(hourAngle)));

        double Ra = (Beta <= 0)
                ? 1e-45
                : ((12 / pi) * 4.92 * dr) *
                (((w2c - w1c) * sin(phi) * sin(delta)) +
                        (cos(phi) * cos(delta) * (sin(w2) - sin(w1))));

        double Rso = (0.75 + 2e-05 * elevation) * Ra;

        double RsRso = (Rs / Rso <= 0.3)
                ? 0.0
                : (Rs / Rso >= 1)
                ? 1.0
                : Rs / Rso;
        double fcd = (1.35 * RsRso - 0.35 <= 0.05)
                ? 0.05
                : (1.35 * RsRso - 0.35 < 1)
                ? 1.35 * RsRso - 0.35
                : 1;

        double Rna = ((1 - 0.23) * Rs) -
                (2.042e-10 *
                        fcd *
                        (0.34 - 0.14 * sqrt(ea)) *
                        pow(tempK, 4)); // cong thuc tinh Rn

        double Ghr = (Rna > 0)
                ? 0.04
                : 0.2; // G for hourly depend on Rna (or Rn in EThourly)

        double Gday = Rna * Ghr;
        double wind2 = wind * (4.87 / (log(67.8 * height - 5.42)));
        double windf = (radiation > 1e-6) ? 0.25 : 1.7;

        return ((0.408 * Delta * (Rna - Gday)) +
                (psi * (66 / tempK) * wind2 * (DPV))) /
                (Delta + (psi * (1 + (windf * wind2))));
    }

    private double getDoy(LocalDateTime dateTime) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime x = LocalDateTime.of(dateTime.getYear(), 1, 1, 0, 0);
            double different = ChronoUnit.DAYS.between(x, dateTime);
            different += dateTime.getHour() / 24.0 + dateTime.getMinute() / 24.0 / 60.0 +
                    dateTime.getSecond() / 24.0 / 3600.0;
            return different;
        }
        return 0;
    }

    //water stress
    private double fWstress(double minv, double maxv, double the) {
        double s = 1 / (maxv - minv);
        double i = -1 * minv * s;
        return (lim(i + s * relTheta(the)));
    }

    private List<Double> multiplyListsWithConstant(List<Double> l, double c) {
        for (int i = 0; i < l.size(); i++) {
            l.set(i, l.get(i) * c);
        }
        return l;
    }

    private double getStress(double clab, double dm, double low, double high, boolean swap) {
        if (high < -9999.0) high = low + 0.01;
        double dm1 = max(dm, 0.001);
        double cc = clab / dm1;
        double rr = lim((cc - low) / (high - low));
        if (swap) rr = 1.0 - rr;
        return (rr);
    }

    private double getStress(double clab, double dm, double low) {
        double dm1 = max(dm, 0.001);
        double cc = clab / dm1;
        return (lim((cc - low) / (-9999.9 - low)));
    }

    private double getStress(double clab, double dm, double low, boolean swap) {
        double dm1 = max(dm, 0.001);
        double cc = clab / dm1;
        double rr = lim((cc - low) / (-9999.9 - low));
        if (swap) rr = 1.0 - rr;
        return (rr);
    }

    private double getStress(double clab, double dm, double low, double high) {
        if (high < -9999.0) high = low + 0.01;
        double dm1 = max(dm, 0.001);
        double cc = clab / dm1;
        return (lim((cc - low) / (high - low)));
    }

    double fNSstress(double upt, double low, double high) {
        double rr = (upt - low) / (high - low);
        return lim(rr);
    }

    double photoFixMean(double ppfd, double lai, double Pn_max) {
        double r = 0;
        int n = 30; //higher more precise, lower faster
        double kdf = -0.47;
        double phi = 0.05553;
        double k = 0.90516;
        double b = 4 * k * Pn_max;
        for (int i = 0; i < n; ++i) {
            double kf = exp(kdf * lai * (i + 0.5) / n);
            double I = ppfd * kf;
            double x0 = phi * I;
            double x1 = x0 + Pn_max;
            double p = x1 - sqrt(x1 * x1 - b * x0);
            r += p;
        }
        r *= -12e-6 * 60 * 60 * 24 * kdf * areaPerPlant * lai / n / (2 * k);
        return (r);
    }

    private List<Double> ode2(double ct, List<Double> y, List<Double> measuredData/* air_humidity, radiation, temperature, soil_humidity */) {
        int cnt = -1;
        double LDM = y.get(++cnt); // Leaf Dry Mass (g)             0
        double SDM = y.get(++cnt); // Stem Dry Mass (g)             1
        double RDM = y.get(++cnt); // Root Dry Mass (g)             2
        double SRDM = y.get(++cnt); // Storage Root Dry Mass (g)    3
        double LA = y.get(++cnt); // Leaf Area (g)                  4
        double mDMl = y.get(++cnt);                             //  5
        double Clab = y.get(++cnt);                             //  6
        List<Double> rootLength = y.subList(7, 12);
        List<Double> rootTips = y.subList(12, 17);
        double NRT = 0;
        for (int i = 0; i < rootTips.size(); i++) {
            NRT += rootTips.get(i);
        }
        List<Double> soilWaterCapacity = y.subList(17, 22);
        List<Double> ncontL = y.subList(22, 27);
        List<Double> nuptL = y.subList(27, 32);
        double Nupt = 0;
        for (int i = 0; i < numberOfSoilLayer; i++) {
            Nupt += nuptL.get(i);
        }

        double TDM = LDM + SDM + RDM + SRDM + Clab; // Total Dry Mass (g)
        double cDm = 0.43; // carbon to dry matter ratio (g C/g DM)

        double leafTemp = measuredData.get(2); // [air_humidity, radiation, temperature, soil_humidity]
        double TSphot = lim(-0.832097717 + 0.124485738 * leafTemp -
                0.002114081 * pow(leafTemp, 2));
        double TSshoot = lim(-1.5 + 0.125 * leafTemp) * lim(7.4 - 0.2 * leafTemp);
        double TSroot = 1.0;

        /*
            Tính thetaEquiv
         */
        List<Double> kroot = new ArrayList<>();
        double Kroot = 0.0;
        for (int i = 0; i < numberOfSoilLayer; i++) {
            double x = fKroot(soilWaterCapacity.get(i), rootLength.get(i));
            kroot.add(x);
            Kroot += x;
        }
        Kroot = Math.max(1e-8, Kroot);

        double thetaEquiv = 0;
        if (Kroot > 1e-8) {
            List<Double> temp = multiplyLists(soilWaterCapacity, kroot);
            double count = 0;
            for (int i = 0; i < temp.size(); i++) {
                count += temp.get(i);
            }
            thetaEquiv = count / Kroot;
        }

        double fcThreshHold = fieldCapacityThreshHold();
        double irrigation = this.irrigationInformation.autoIrrigation ? y.get(32) : 0.0;
        // convert from hour to day
        double autoIrrigationDuration = (y.get(32)) / 24;
        double autoIrrigate = customizedParameter.dripRate * 24.0 /(customizedParameter.distanceBetweenHole *
                customizedParameter.distanceBetweenRow / 10000.0);

        if (irrigation < 1e-6 && autoIrrigationTime < ct + autoIrrigationDuration &&
                fcThreshHold > thetaR && thetaEquiv < fcThreshHold) {
            autoIrrigationTime = ct;
        }
        if (ct < autoIrrigationTime + autoIrrigationDuration) {
            irrigation += autoIrrigate;
        }

        double rain;
        int[] rainRate = {0, 0, 0, 0, 1};
        int rate = new Random().nextInt(5);
        if (rainRate[rate] == 0) {
            rain = 0;
        } else {
            rain = new Random().nextDouble();
        }
        double precipitation = customizedParameter.scaleRain / 100 * rain + irrigation;

        // Lượng thoát hơi nước Transpiration
        double wind = new Random().nextDouble() * 6;
        double doy = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            doy = getDoy(LocalDateTime.now());
        }
        // vi do
        double latitude = 21.0075;
        // kinh do
        double longitude = 105.5416;
        // do cao so voi muc nuoc bien
        double elevation = 16;
        // do cao
        double height = 2.5;
        double ET0reference = hourlyET(measuredData.get(2),
                measuredData.get(1),
                measuredData.get(0),
                wind,
                doy,
                latitude, longitude, elevation, longitude, height); // [air_humidity, radiation, temperature, soil_humidity]
        double ETrainFactor = (precipitation > 0) ? 1 : 0;
        double kdf = -0.47;
        double ll = exp(kdf * LA / areaPerPlant);
        double cropFactor = max(1 - ll * 0.8, ETrainFactor);
        double transpiration = cropFactor * ET0reference; //su thoat hoi nuoc

        double WStrans = fWstress(0.05, 0.5, thetaEquiv);
        double WSphot = fWstress(0.05, 0.3, thetaEquiv);
        double WSshoot = fWstress(0.2, 0.55, thetaEquiv);
        double WSroot = 1;
        double WSleafSenescence = 1.0 - fWstress(0.0, 0.2, thetaEquiv);

        double actualTranspiration = transpiration * WStrans; // uptake in liter/day per m2

        // Lượng nước do rễ hấp thụ rwui
        List<Double> wuptrL = multiplyListsWithConstant(kroot, actualTranspiration / Kroot);
        double countWuptrL = 0;
        for (int i = 0; i < wuptrL.size(); i++) {
            countWuptrL += wuptrL.get(i);
        }

        // Hàm lượng nước trong các lớp đất
        double swfe = pow(relTheta(soilWaterCapacity.get(0)), 2.5);
        double actFactor = max(ll * swfe, ETrainFactor);
        double evaporation = actFactor * ET0reference; // su bay hoi

        double drain;
        List<Double> qFlow = new ArrayList<>();
        for (int i = 0; i < numberOfSoilLayer + 1; i++) {
            qFlow.add(0.0);
        }

        qFlow.set(0, (precipitation - evaporation) / (layerThickness * 1000.0));
        for (int i = 1; i < qFlow.size(); ++i) {
            double thetaM = 0.18;
            double thdown = (i < numberOfSoilLayer)
                    ? soilWaterCapacity.get(i)
                    : (zerodrain)
                    ? soilWaterCapacity.get(i - 1) + thetaG
                    : thetaM;
            double rateFlow = 1.3;
            qFlow.set(i, (soilWaterCapacity.get(i - 1) + thetaG - thdown) * rateFlow * (soilWaterCapacity.get(i - 1) / thetaS) +
                    4.0 * max(soilWaterCapacity.get(i - 1) - thetaS, 0));
        }

        List<Double> dThetaDt = new ArrayList<>();
        for (int i = 0; i < numberOfSoilLayer; i++) {
            dThetaDt.add(qFlow.get(i) - qFlow.get(i + 1) - wuptrL.get(i) / (layerThickness * 1000.0));
        }
        drain = qFlow.get(numberOfSoilLayer) * layerThickness * 1000;

        // nutrient concentrations in the plant
        double Nopt = 45 * LDM + 7 * SRDM + 20 * SDM + 20 * RDM;
        double NuptLimiter = 1.0 - fNSstress(Nupt, 2.0 * Nopt, 3.0 * Nopt);
        //nutrient uptake
        List<Double> nuptrL = new ArrayList<>();
        double bulkDensity = 1380;
        for (int i = 0; i < numberOfSoilLayer; i++) {
            nuptrL.add(monod(ncontL.get(i) * bulkDensity / (1000 * soilWaterCapacity.get(i)),
                    NuptLimiter * rootLength.get(i) * 0.8));
        }

        List<Double> ncontrL = Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0); //mg/kg/day
        List<Double> NminR_l = new ArrayList<>();
        // volume of one soil layer
        double layerVolume = layerThickness * areaPerPlant;
        for (int i = 0; i < numberOfSoilLayer; i++) {
            NminR_l.add(customizedParameter.fertilizationLevel / 100 * 36 / (layerVolume * bulkDensity) / pow(i + 1, 2));
        }
        for (int i = 0; i < numberOfSoilLayer; ++i) {
            ncontrL.set(i, NminR_l.get(i));
            ncontrL.set(i, nuptrL.get(i) / (bulkDensity * layerVolume)); //mg/day/ (m3*kg/m3)
            double Nl = ncontL.get(i);
            double Nu = (i > 0) ? ncontL.get(i - 1) : -ncontL.get(i);
            //double Nd = (i < (numberOfSoilLayer - 1)) ? Ncont_l[i + 1] : -Ncont_l[i];//zero flux bottom
            double Nd = (i < (numberOfSoilLayer - 1)) ? ncontL.get(i + 1) : 0.0; //leaching
            // no diffusion, just mass flow with water.
            ncontrL.set(i, qFlow.get(i) * (Nu + Nl) / 2.0 - qFlow.get(i + 1) * (Nl + Nd) / 2.0);
        }

        //double NcontR_l =  substractLists(NminR_l, NuptR_l); // change in N in soil (mg/day)
        double NSphot = (Nopt > 1e-3) ? fNSstress(Nupt, 0.7 * Nopt, Nopt) : 1.0;
        double NSshoot = (Nopt > 1e-3) ? fNSstress(Nupt, 0.7 * Nopt, 0.9 * Nopt) : 1.0;
        double NSroot =
                (Nopt > 1e-3) ? fNSstress(Nupt, 0.5 * Nopt, 0.7 * Nopt) : 1.0;
        //double NSsroot = 1.0;
        // 1 for fast leaf senescence when plant is stressed for N
        double NSleafSenescence = (Nopt > 1.0) ? 1.0 - fNSstress(Nupt, 0.8 * Nopt, Nopt) : 0.0;

        // sink strength
        double mGRl = logistic(ct, 0.3, 70, 0, 0.9);
        double _leafAge = 75;
        double mGRld = logistic(ct, 0.0, 70.0 + _leafAge, 0.1, -0.90);
        double mGRs = logistic(ct, 0.2, 95, 0.219, 1.87) +
                logistic(ct, 0.0, 209, 0.219, 1.87 - 0.84);
        double mGRr = 0.02 + (0.2 + exp(-0.8 * ct - 0.2)) * mGRl;
        double mGRsr = min(7.08, pow(max(0.0, (ct - 32.3) * 0.02176), 2));

        // carbon limitations
        double CSphot = getStress(Clab, TDM, 0.05, true); //Lower photosynthesis when starche accumulates
        double CSshoota = getStress(Clab, TDM, -0.05); //do not allocat to shoot when starche levels are low
        double CSshootl = lim(5 - LA / areaPerPlant); // do not allocat to shoot when LAI is high
        double CSshoot = CSshoota * CSshootl;
        double CSroot = getStress(Clab, TDM, -0.03);
        double CSsrootl = getStress(Clab, TDM, -0.0);
        double CSsrooth = getStress(Clab, TDM, 0.01, 0.20);
        double starchRealloc = getStress(Clab, TDM, -0.2, -0.1, true) * -0.05 * SRDM;
        double CSsroot = CSsrootl + 2 * CSsrooth;
        double SFleaf = WSshoot * NSshoot * TSshoot * CSshootl;
        double SFstem = WSshoot * NSshoot * TSshoot * CSshoot;
        double SFroot = WSroot * NSroot * TSroot * CSroot;
        double SFsroot = CSsroot;

        double CsinkL = cDm * mGRl * SFleaf; //*
        double CsinkS = cDm * mGRs * SFstem; //* ((mDMs > 30 && ct > 500) ? SDM / mDMs : 1);
        double CsinkR = cDm * mGRr * SFroot; //* ((mDMr > 1 && ct > 300) ? RDM / mDMr : 1);
        double CsinkSR = cDm * mGRsr * SFsroot - starchRealloc;
        double Csink = CsinkL + CsinkS + CsinkR + CsinkSR;

        // biomass partitioning
        double a2l = CsinkL / max(1e-10, Csink);
        double a2s = CsinkS / max(1e-10, Csink);
        double a2r = CsinkR / max(1e-10, Csink);
        double a2sr = CsinkSR / max(1e-10, Csink);

        // carbon to growth
        double CFG = Csink;
        // increase in plant dry Mass (g DM/day) not including labile carbon pool
        double IDM = Csink / cDm;

        //photosynthesis
        double PPFD = 2.5 * measuredData.get(1); // [air_humidity, radiation, temperature, soil_humidity]
        double SFphot = min(min(TSphot, WSphot), min(NSphot, CSphot));
        double CFR = photoFixMean(PPFD, LA / areaPerPlant, 29.37 * SFphot);

        double SDMR = a2s * IDM; // stem growth rate (g/day)
        double SRDMR = a2sr * IDM; // storage root growth rate (g/day)

        double SLA = fSLA(ct);
        double LDRstress = WSleafSenescence * NSleafSenescence * LDM * -1.0;
        double LDRage = mGRld * ((mDMl > 0) ? LDM / mDMl : 1.0);
        double LDRm = max(-LDM, LDRstress + LDRage);
        double LDRa = max(-LA, fSLA(max(0.0, ct - _leafAge)) * LDRm);
        double LAeR = SLA * a2l * IDM + LDRa; // Leaf Area expansion Rate (m2/day)
        double LDMR = a2l * IDM + LDRm;
        double RDMR = a2r * IDM; // fine root growth rate (g/day)
        //m/g
        double _SRL = 39.0;
        double RLR = _SRL * RDMR;
        List<Double> rlrL = new ArrayList<Double>();
        for (int i = 0; i < numberOfSoilLayer; i++) {
            rlrL.add(RLR * rootTips.get(i) / NRT);
        }

        double ln0 = 0.0;
        List<Double> nrtrL = new ArrayList<>();
        for (int i = 0; i < numberOfSoilLayer; ++i) {
            double ln1 = rlrL.get(i);
            nrtrL.add(i, ln1 * 60.0 + max(0, (ln0 - ln1 - 6.0 * layerThickness) * 10.0 / layerThickness));
            ln0 = ln1;
        }

        // respiration
        double mRR = 0.003 * RDM + 0.0002 * SRDM + 0.003 * LDM + 0.0002 * SDM;
        double gRR = 1.8 * RDMR + 0.2 * SRDMR + 1.8 * (LDMR - LDRm) + 0.4 * SDMR;
        double RR = mRR + gRR;

        // labile pool
        double ClabR = (CFR - CFG - RR) / cDm;

        precipitation -= irrigation;
        irrigation = abs(evaporation + countWuptrL - precipitation);
        Log.e("evaporation", String.valueOf(evaporation));
        Log.e("countWuptrL", String.valueOf(countWuptrL));
        Log.e("precipitation", String.valueOf(precipitation));
        double _irrigation = max(0.0, irrigation);
        while (_irrigation < 0.01) {
            _irrigation *= 10;
        }

        List<Double> YR = new ArrayList<>();
        YR.add(LDMR);// 0
        YR.add(SDMR);// 1
        YR.add(RDMR);//  2
        YR.add(SRDMR);// 3
        YR.add(LAeR);// 4

        YR.add(mGRl);// 5
        YR.add(ClabR);// 6

        YR.addAll(rlrL);
        YR.addAll(nrtrL);
        YR.addAll(dThetaDt);
        YR.addAll(ncontrL);
        YR.addAll(nuptrL);

        YR.add(_irrigation);

        return YR;
    }

    private void intStep(List<Double> treeData, List<Double> n, double dt) {
        assert (treeData.size() == n.size());
        for (int i = 0; i < treeData.size(); ++i) {
            double oldValue = treeData.get(i);
            treeData.set(i, oldValue + dt * n.get(i));
        }
    }

    private void rk4Step(double t, List<Double> treeData, double dt, List<Double> measuredData) {
        List<Double> yCopy = new ArrayList<>(treeData);

        List<Double> r1 = ode2(t, yCopy, measuredData);
        double t1 = t + dt * 0.5;
        double t2 = dt + t;

        intStep(yCopy, r1, 0.5 * dt);
        List<Double> r2 = ode2(t1, yCopy, measuredData);
        for (int i = 0; i < treeData.size(); i++)  yCopy.set(i, treeData.get(i)); // reset

        intStep(yCopy, r2, 0.5 * dt);
        List<Double> r3 = ode2(t1, yCopy, measuredData);
        for (int i = 0; i < treeData.size(); i++)  yCopy.set(i, treeData.get(i)); // reset

        intStep(yCopy, r3, dt);
        List<Double> r4 = ode2(t2, yCopy, measuredData);
        for (int i = 0; i < r4.size(); i++) {
            r4.set(i, (r1.get(i) + 2 * (r2.get(i) + r3.get(i)) + r4.get(i)) / 6);
        }

        intStep(treeData, r4, dt);
    }
}
