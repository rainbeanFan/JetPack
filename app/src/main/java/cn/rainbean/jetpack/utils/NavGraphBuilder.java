package cn.rainbean.jetpack.utils;

import android.content.ComponentName;

import java.util.HashMap;
import java.util.Iterator;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavGraphNavigator;
import androidx.navigation.NavigatorProvider;
import cn.rainbean.basemodule.GlobalApp;
import cn.rainbean.jetpack.module.Destination;
import cn.rainbean.jetpack.navigator.FixFragmentNavigator;

public class NavGraphBuilder {


    public static void build(FragmentActivity activity, FragmentManager childFragmentManager,
                             NavController controller,int containerId){

        NavigatorProvider provider = controller.getNavigatorProvider();

        NavGraph navGraph = new NavGraph(new NavGraphNavigator(provider));

        FixFragmentNavigator fragmentNavigator = new FixFragmentNavigator(activity, childFragmentManager, containerId);
        provider.addNavigator(fragmentNavigator);

        ActivityNavigator activityNavigator = provider.getNavigator(ActivityNavigator.class);

        HashMap<String, Destination> destConfig = AppConfig.getDestConfig();

        Iterator<Destination> iterator = destConfig.values().iterator();

        while (iterator.hasNext()){
            Destination node = iterator.next();
            if (node.isFragment){
                FixFragmentNavigator.Destination destination = fragmentNavigator.createDestination();
                destination.setId(node.id);
                destination.setClassName(node.className);
                destination.addDeepLink(node.pageUrl);
                navGraph.addDestination(destination);
            }else {
                ActivityNavigator.Destination destination = activityNavigator.createDestination();
                destination.setId(node.id);
                destination.setComponentName(new ComponentName(GlobalApp.getApplication().getPackageName(),node.className));
                destination.addDeepLink(node.pageUrl);
                navGraph.addDestination(destination);
            }

            if (node.asStarter){
                navGraph.setStartDestination(node.id);
            }

        }

        controller.setGraph(navGraph);

    }



}
