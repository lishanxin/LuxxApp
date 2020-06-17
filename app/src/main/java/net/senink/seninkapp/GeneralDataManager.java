package net.senink.seninkapp;

import android.content.Context;

import com.telink.sig.mesh.model.DeviceInfo;
import com.telink.sig.mesh.model.Group;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.telink.api.TelinkApiManager;
import net.senink.seninkapp.telink.model.TelinkBase;
import net.senink.seninkapp.ui.util.SortUtils;

import org.spongycastle.pqc.crypto.gmss.util.WinternitzOTSignature;

import java.util.ArrayList;
import java.util.List;

public class GeneralDataManager {

    private static final String TAG = GeneralDataManager.class.getSimpleName();
    private static GeneralDataManager instance;
    private Context mContext;
    private PISManager pm;
    public static GeneralDataManager getInstance() {
        if (instance == null) {
            synchronized (GeneralDataManager.class) {
                if (instance == null) {
                    instance = new GeneralDataManager();
                }
            }
        }
        return instance;
    }

    public void init(Context context, PISManager pm) {
        mContext = context;
        this.pm = pm;
    }

    public void unInit(){
        mContext = null;
        this.pm = null;
    }

    private int lastCurShowListType;

    public List<GeneralDeviceModel[]> getGeneralDeviceAndGroups(int curShowListType){
        lastCurShowListType = curShowListType;

        return getGeneralDeviceAndGroups();
    }

    public List<GeneralDeviceModel[]> getGeneralDeviceAndGroups(){
        List<GeneralDeviceModel[]> generalDeviceModels = new ArrayList<>();
        List<GeneralDeviceModel> generalDevice = getGeneralDevice(lastCurShowListType);
        List<GeneralDeviceModel> generalGroup = getGeneralGroup(lastCurShowListType);

        if (generalDevice != null && generalDevice.size() > 0){
            generalDeviceModels.addAll(SortUtils.sortGeneralServiceFor4(generalDevice));
        }
        if(generalGroup != null && generalGroup.size() > 0){
            generalDeviceModels.addAll(SortUtils.sortGeneralServiceFor4(generalGroup));
        }

        return generalDeviceModels;
    }

    public List<GeneralDeviceModel> getGeneralGroup(int curShowListType){
        //更新ProductClassifyView状态
        if(pm == null ) return null;
        //更新Fragments状态
        // TODO LEE 灯组及灯列表刷新
        List<PISBase> srvsGroup = getPISGroups(curShowListType);

        List<GeneralDeviceModel> generalGroup = new ArrayList<>();

        List<Group> telinkGroup = MyApplication.getInstance().getMesh().groups;
        outerGroup:
        for (Group group : telinkGroup) {
            if(group.type == Group.BOUND_TYPE.NONE){
                if(pm.getPISObject(group.PISKeyString) == null){
                    group.PISKeyString = "";
                    group.type = Group.BOUND_TYPE.TELINK_GROUP;
                }
            }
            for (PISBase base : srvsGroup) {
                if(base.getPISKeyString().equals(group.PISKeyString)){
                    List<PISBase> sublist = base.getGroupObjects();
                    if(sublist != null && sublist.size() > 0 && sublist.get(0) != null){
                        if(group.type == Group.BOUND_TYPE.NONE ){
                            group.type = Group.BOUND_TYPE.TELINK_GROUP;
                            group.PISKeyString = "";
                        }else if(group.type == Group.BOUND_TYPE.PIS_GROUP){
                            telinkGroup.remove(group);
                            MyApplication.getInstance().getMesh().saveOrUpdate(mContext);
                            continue outerGroup;
                        }
                    }else{
                        srvsGroup.remove(base);
                    }
                    break;
                }
            }
//            if(group.type == Group.BOUND_TYPE.TELINK_GROUP){
//                group.isOn = false;
//                for (Integer meshAddress : group.subList) {
//                    DeviceInfo deviceInfo = MyApplication.getInstance().getMesh().getDeviceByMeshAddress(meshAddress);
//                    if(deviceInfo.getOnOff() == 1){
//                        group.isOn = true;
//                        break;
//                    }
//                }
//            }
            generalGroup.add(new GeneralDeviceModel(new TelinkBase(group)));
        }

        if (srvsGroup.size() > 0){
            for (PISBase pisBase : srvsGroup) {
                generalGroup.add(new GeneralDeviceModel(pisBase));
            }
        }

        return generalGroup;
    }

    private List<PISBase> getPISGroups(int curShowListType){
        List<Integer> pistypes = getPisTypes(curShowListType);
        // TODO LEE 灯组及灯列表刷新
        List<PISBase> srvsGroup = new ArrayList<>();
        // 刷新灯组列表
        for (Integer i : pistypes){
            List<PISBase> groupsPis = pm.PIGroupsWithQuery(i, PISManager.EnumGroupsQueryBaseonType);
            if (groupsPis != null && groupsPis.size() > 0){
                srvsGroup.addAll(groupsPis);
            }
        }
        return srvsGroup;
    }

    public List<PISBase> getPISGroups(){
        return getPISGroups(lastCurShowListType);
    }

    public List<GeneralDeviceModel> getGeneralDevice(int curShowListType){
//更新ProductClassifyView状态
        if(pm == null ) return null;
        //更新Fragments状态
        ArrayList<PISBase[]> list = new ArrayList<PISBase[]>();
        List<Integer> pistypes = getPisTypes(curShowListType);

        // TODO LEE 灯组及灯列表刷新
        List<PISBase> srvsDevice = new ArrayList<>();
        // 刷新灯组列表
        for (Integer i : pistypes){
            List<PISBase> devicesPis = pm.PIServicesWithQuery(i, PISManager.EnumServicesQueryBaseonType);
            if (devicesPis != null && devicesPis.size() > 0){
                srvsDevice.addAll(devicesPis);
            }
        }

        List<GeneralDeviceModel> generalDevice = new ArrayList<>();
        for (DeviceInfo device : MyApplication.getInstance().getMesh().devices) {
            generalDevice.add(new GeneralDeviceModel(new TelinkBase(device)));
        }
        if (srvsDevice.size() > 0){
            for (PISBase pisBase : srvsDevice) {
                generalDevice.add(new GeneralDeviceModel(pisBase));
            }
        }

        return generalDevice;
    }

    private List<Integer> getPisTypes(int curShowListType){
        ArrayList<Integer> pistypes = new ArrayList<Integer>();

        switch (curShowListType){
            case PISManager.PISERVICE_CATEGORY_SOCKET:
                pistypes.add(PISConstantDefine.PIS_MAJOR_ELECTRICIAN | (PISConstantDefine.PIS_ELECTRICIAN_SWITCH_POWER<<8));
                pistypes.add(PISConstantDefine.PIS_MAJOR_ELECTRICIAN | (PISConstantDefine.PIS_ELECTRICIAN_NORMAL<<8));
                break;
            case PISManager.PISERVICE_CATEGORY_LIGHT:
                pistypes.add(PISConstantDefine.PIS_MAJOR_LIGHT | (PISConstantDefine.PIS_LIGHT_COLOR<<8));
                pistypes.add(PISConstantDefine.PIS_MAJOR_LIGHT | (PISConstantDefine.PIS_LIGHT_LIGHT<<8));
                pistypes.add(PISConstantDefine.PIS_MAJOR_LIGHT | (PISConstantDefine.PIS_LIGHT_COLORLIGHT<<8));		// NextApp.tw

                pistypes.add(PISConstantDefine.PIS_MAJOR_MULTIMEDIA | (PISConstantDefine.PIS_MULTIMEDIA_COLOR<<8));
                break;
            case PISManager.PISERVICE_CATEGORY_BRIDGE:
                pistypes.add(PISConstantDefine.PIS_MAJOR_NETWORK | (PISConstantDefine.PIS_NETWORK_CSRMESH<<8));
                break;
            case PISManager.PISERVICE_CATEGORY_REMOTER:
                pistypes.add(PISConstantDefine.PIS_MAJOR_REMOTER | (PISConstantDefine.PIS_REMOTER_KEY16<<8));
                break;
            case PISManager.PISERVICE_CATEGORY_WEAR:
                pistypes.add(PISConstantDefine.PIS_MAJOR_WEARABLE | (PISConstantDefine.PIS_WEARABLE_INSOLE<<8));
                break;
        }
        return pistypes;
    }
}
