package cn.rainbean.jetpack.navigator;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;
import androidx.navigation.fragment.FragmentNavigator;

@Navigator.Name("fixFragment")
public class FixFragmentNavigator extends FragmentNavigator {

    private static final String TAG = "FixFragmentNavigator";

    private Context mContext;

    private FragmentManager mManager;

    private int mContainerId;

    public FixFragmentNavigator(@NonNull Context context, @NonNull FragmentManager manager,
                                int containerId) {
        super(context, manager, containerId);
        mContext = context;
        mManager = manager;
        mContainerId = containerId;
    }

    @Nullable
    @Override
    public NavDestination navigate(@NonNull Destination destination, @Nullable Bundle args,
                                   @Nullable NavOptions navOptions, @Nullable Navigator.Extras navigatorExtras) {

        if (mManager.isStateSaved()){
            return null;
        }

        String className = destination.getClassName();
        if (className.charAt(0) == '.'){
            className = mContext.getPackageName()+className;
        }

        String tag = className.substring(className.lastIndexOf(".") + 1);
        Fragment fragment = mManager.findFragmentByTag(tag);
        if (fragment == null){
            fragment = instantiateFragment(mContext,mManager,className,args);
        }
        fragment.setArguments(args);
        FragmentTransaction fragmentTransaction = mManager.beginTransaction();

        int enterAnim = navOptions!=null?navOptions.getEnterAnim():-1;
        int exitAnim = navOptions!=null?navOptions.getExitAnim():-1;
        int popEnterAnim = navOptions!=null?navOptions.getPopEnterAnim():-1;
        int popExitAnim = navOptions!=null?navOptions.getPopExitAnim():-1;
        if (enterAnim!=-1 || exitAnim!=-1||popEnterAnim!=-1||popExitAnim!=-1){
            enterAnim = enterAnim!=-1?enterAnim:0;
            exitAnim = exitAnim!=-1?exitAnim:0;
            popEnterAnim = popEnterAnim!=-1?popEnterAnim:0;
            popExitAnim = popExitAnim!=-1?popExitAnim:0;
            fragmentTransaction.setCustomAnimations(enterAnim,exitAnim,popEnterAnim,popExitAnim);
        }

        List<Fragment> fragments = mManager.getFragments();
        for (Fragment f:fragments) {
            fragmentTransaction.hide(f);
        }

        if (!fragment.isAdded()){
            fragmentTransaction.add(mContainerId,fragment,tag);
        }
        fragmentTransaction.show(fragment);
        fragmentTransaction.setPrimaryNavigationFragment(fragment);

        int destinationId = destination.getId();
        ArrayDeque<Integer> mBackStack = null;
        try{
            Field field = FragmentNavigator.class.getDeclaredField("mBackStack");
            field.setAccessible(true);
            mBackStack = (ArrayDeque<Integer>) field.get(this);
        }catch (NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
        }

        boolean initialNavigation = mBackStack.isEmpty();

        boolean isSingleTopReplacement = navOptions!=null && !initialNavigation
                && navOptions.shouldLaunchSingleTop() && mBackStack.peekLast() == destinationId;

        boolean isAdded;
        if (initialNavigation){
            isAdded = true;
        }else if (isSingleTopReplacement){
            if (mBackStack.size()>1){
                mManager.popBackStack(
                        generateBackStackName(mBackStack.size(),mBackStack.peekLast()),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                );
                fragmentTransaction.addToBackStack(generateBackStackName(mBackStack.size(),destinationId));
            }
            isAdded = false;
        }else {
            fragmentTransaction.addToBackStack(generateBackStackName(mBackStack.size()+1,destinationId));
            isAdded = true;
        }

        if (navigatorExtras instanceof Extras){
            Extras extras = (Extras) navigatorExtras;
            for (Map.Entry<View,String> sharedElement:extras.getSharedElements().entrySet()) {
                fragmentTransaction.addSharedElement(sharedElement.getKey(),sharedElement.getValue());
            }
        }

        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commit();

        if (isAdded){
            mBackStack.add(destinationId);
            return destination;
        }else {
            return null;
        }
    }

    private String generateBackStackName(int backStackIndex, int destId) {
        return backStackIndex + "-" + destId;
    }

}
