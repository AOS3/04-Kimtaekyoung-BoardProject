# 01_프로젝트 기본설정

### DataBinding 설정

[build.gradle.kts]
```kt
    dataBinding{
        enable = true
    }
```

```kt
dependencies {
    ...
    implementation("androidx.fragment:fragment-ktx:1.8.5")
}
```

### 아이콘 파일 복사
- 제공해드린 아이콘 파일들을 res/drawable 폴더에 넣어주세요

---

# 02_앱 아이콘 설정

### 프로젝트에서 마우스 우클릭 > new > Image Assets 도구를 통해 만들어준다.

### AndroidManifest.xml 에 아이콘을 등록해준다.

```xml
        android:icon="@mipmap/like_lion_icon"
        android:roundIcon="@mipmap/like_lion_icon_round"
```


### 어플 이름을 변경해준다.

[res/values/strings.xml]
```xml
    <string name="app_name">멋쟁이사자처럼게시판</string>
```

--- 

# 03_Splash Screen 구성

### 프로젝트에서 마우스 우클릭 > new > Image Assets 도구를 통해 아이콘을 만들어준다.

### 브랜드 이미지로 사용할 이미지를 drawable 폴더에 넣어준다.

### 라이브러리를 추가한다.

[build.gradle.kts]
```kt
implementation("androidx.core:core-splashscreen:1.0.1")
```

### SplashScreen용 테마를 작성한다.

[res/values/themes.xml]

```xml
    <style name="AppTheme.Splash" parent="Theme.Material3.DayNight.NoActionBar">
        <!-- 배경 색상 또는 이미지 -->
        <item name="windowSplashScreenBackground">@color/white</item>
        <!-- 중앙에 표시될 아이콘 이미지 -->
        <item name="windowSplashScreenAnimatedIcon">@mipmap/like_lion_logo</item>
        <!-- Splash Screen이 보여질 시간 -->
        <item name="windowSplashScreenAnimationDuration">1000</item>
        <!-- windowSplashScreenAnimationDuration 에서 지정한 시간이 끝나면 적용할 테마 -->
        <!-- AndroidManifest.xml 에 적용되어 있는 테마를 적용해준다 -->
        <item name="postSplashScreenTheme">@style/Theme.BoardProject</item>
    </style>
```

### MainActivity의 테마를 설정해준다.

[AndroidManifest.xml]
```xml
        <activity
            ...
            android:theme="@style/AppTheme.Splash">
```

### SplashScreen 관련 메서드를 호출해준다.

[MainActivity.kt - onCreate()]
```kt
        // SplashScreen 적용
        installSplashScreen()
```

---

# 04_회원관련 Activity 구성

### UserActivity를 만들어준다.

### MainActivity를 종료하고 UserActivity를 실행한다.

[MainActivity.kt]
```kt
        // UserActivity를 실행한다.
        val userIntent = Intent(this@MainActivity, UserActivity::class.java)
        startActivity(userIntent)
        // MainActivity를 종료한다.
        finish()
```

### UserActivity의 화면을 구성한다.

[res/layout/activity_user.xml]
```xml
<?xml version="1.0" encoding="utf-8"?>
<layout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:id="@+id/main"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context=".UserActivity">

        <androidx.fragment.app.FragmentContainerView
            android:id="@+id/fragmentContainerViewUser"
            android:layout_width="0dp"
            android:layout_height="0dp"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent" />
    </androidx.constraintlayout.widget.ConstraintLayout>

</layout>

```

### DataBinding 코드를 작성한다

[UserActivity.kt]
```kt
    lateinit var activityUserBinding:ActivityUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        activityUserBinding = DataBindingUtil.setContentView(this@UserActivity, R.layout.activity_user)
```

### 프래그먼트들의 이름을 가지고 있는 enum class를 작성한다.

[UserActivity.kt]
```kt
// 프래그먼트들을 나타내는 값들
enum class UserFragmentName(var number:Int, var str:String){

}
```

### 프래그먼트를 제거하는 메서드를 구현한다.

[UserActivity.kt]
```kt
    // 프래그먼트를 BackStack에서 제거하는 메서드
    fun removeFragment(fragmentName: UserFragmentName){
        supportFragmentManager.popBackStack(fragmentName.str, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
```

### 프래그먼트를 담을 변수를 선언한다.

[UserActivity.kt]
```kt
    // 현재 Fragment와 다음 Fragment를 담을 변수(애니메이션 이동 때문에...)
    var newFragment:Fragment? = null
    var oldFragment:Fragment? = null
```

### 프래그먼트 교체 메서드를 구현한다.

[UserActivity.kt]
```kt

    // 프래그먼트를 교체하는 함수
    fun replaceFragment(fragmentName: UserFragmentName, isAddToBackStack:Boolean, animate:Boolean, dataBundle: Bundle?){
        // newFragment가 null이 아니라면 oldFragment 변수에 담아준다.
        if(newFragment != null){
            oldFragment = newFragment
        }
        // 프래그먼트 객체
        newFragment = when(fragmentName){

        }

        // bundle 객체가 null이 아니라면
        if(dataBundle != null){
            newFragment?.arguments = dataBundle
        }

        // 프래그먼트 교체
        supportFragmentManager.commit {

            if(animate) {
                // 만약 이전 프래그먼트가 있다면
                if(oldFragment != null){
                    oldFragment?.exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
                    oldFragment?.reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
                }

                newFragment?.exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
                newFragment?.reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
                newFragment?.enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
                newFragment?.returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
            }

            replace(R.id.fragmentContainerViewUser, newFragment!!)
            if(isAddToBackStack){
                addToBackStack(fragmentName.str)
            }
        }
    }
```

--- 

# 05_로그인 화면 구성

### 두 개의 패키지를 만들어준다.
- fragment
- viewmodel

### LoginFragment를 만들어준다.

### LoginViewModel을 만들어준다.

[viewmodel/LoginViewModel.kt]
```kt
data class LoginViewModel(val loginFragment: LoginFragment) : ViewModel() {
}
```

### layout 태그로 묶어준다.

[res/layout/fragment_login.xml]

```xml
<?xml version="1.0" encoding="utf-8"?>
<layout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <data>
        <variable
            name="loginViewModel"
            type="com.lion.boardproject.viewmodel.LoginViewModel" />
    </data>

    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context=".fragment.LoginFragment">

        <!-- TODO: Update blank fragment layout -->
        <TextView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:text="@string/hello_blank_fragment" />

    </FrameLayout>

</layout>

```

### 프래그먼트의 기본 코드를 작성한다.

[fragment/LoginFragment.kt]
```kt
class LoginFragment : Fragment() {

    lateinit var fragmentLoginBinding: FragmentLoginBinding
    lateinit var userActivity: UserActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentLoginBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        fragmentLoginBinding.loginViewModel = LoginViewModel(this@LoginFragment)
        fragmentLoginBinding.lifecycleOwner = this@LoginFragment

        userActivity = activity as UserActivity

        return fragmentLoginBinding.root
    }
}
```

### Fragment의 이름을 정의해준다.

[UserActivity.kt]
```kt
    // 로그인 화면
    USER_LOGIN_FRAGMENT(1, "UserLoginFragment"),
```

### Fragment의 객체를 생성한다.

[UserActivity.kt - replaceFragment()]
```kt
            // 로그인 화면
            UserFragmentName.USER_LOGIN_FRAGMENT -> LoginFragment()
```

### 첫 번째 프래그먼트를 설정해준다.

[UserActivity.kt - onCreate()]
```kt
        // 첫번째 Fragment를 설정한다.
        replaceFragment(UserFragmentName.USER_LOGIN_FRAGMENT, false, false, null)
```

### 입력 요소의 digit에 설정할 문자열을 만들어준다.

[res/values/stings.xml]
```xml 
    <!-- 영어, 숫자, 특수문자만 입력을 위한 문자열 -->
    <string name="digit_value">abcdefghijklmnopqrstuvwxyz0123456789_!@#</string>
```

### 화면을 구성한다.

[res/layout/fragment_login.xml]
```xml
<?xml version="1.0" encoding="utf-8"?>
<layout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">

    <data>
        <variable
            name="loginViewModel"
            type="com.lion.boardproject.viewmodel.LoginViewModel" />
    </data>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        android:transitionGroup="true">

        <com.google.android.material.appbar.MaterialToolbar
            android:id="@+id/toolbarUserLogin"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="@android:color/transparent"
            android:minHeight="?attr/actionBarSize"
            android:theme="?attr/actionBarTheme" />

        <ScrollView
            android:layout_width="match_parent"
            android:layout_height="match_parent">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:padding="10dp" >

                <com.google.android.material.textfield.TextInputLayout
                    android:id="@+id/textFieldUserLoginId"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:hint="아이디"
                    app:endIconMode="clear_text"
                    app:startIconDrawable="@drawable/person_24px">

                    <com.google.android.material.textfield.TextInputEditText
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:digits="@string/digit_value"
                        android:singleLine="true"
                        android:textAppearance="@style/TextAppearance.AppCompat.Large" />
                </com.google.android.material.textfield.TextInputLayout>

                <com.google.android.material.textfield.TextInputLayout
                    android:id="@+id/textFieldUserLoginPw"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:hint="비밀번호"
                    app:endIconMode="password_toggle"
                    app:startIconDrawable="@drawable/key_24px"
                    android:layout_marginTop="10dp">

                    <com.google.android.material.textfield.TextInputEditText
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:digits="@string/digit_value"
                        android:singleLine="true"
                        android:inputType="text|textPassword"
                        android:textAppearance="@style/TextAppearance.AppCompat.Large" />
                </com.google.android.material.textfield.TextInputLayout>

                <CheckBox
                    android:id="@+id/checkBoxUserLoginAuto"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="10dp"
                    android:text="자동 로그인"
                    android:textAppearance="@style/TextAppearance.AppCompat.Large" />

                <Button
                    android:id="@+id/buttonUserLoginSubmit"
                    style="@style/Widget.Material3.Button.OutlinedButton"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="10dp"
                    android:text="로그인"
                    android:textAppearance="@style/TextAppearance.AppCompat.Large" />

                <com.google.android.material.divider.MaterialDivider
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="10dp" />

                <Button
                    android:id="@+id/buttonUserLoginJoin"
                    style="@style/Widget.Material3.Button.OutlinedButton"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="10dp"
                    android:text="회원 가입"
                    android:textAppearance="@style/TextAppearance.AppCompat.Large" />
            </LinearLayout>
        </ScrollView>
    </LinearLayout>

</layout>

```

### LiveData를 작성한다.

[viewmodel/LoginViewModel.kt]
```kt
    // toolbarUserLogin - title
    val toolbarUserLoginTitle = MutableLiveData<String>()
```

### layout 파일의 툴바에 LiveData를 설정해준다.

[res/layout/fragment_login.xml]
```xml
        <com.google.android.material.appbar.MaterialToolbar
            ...
            app:title="@{loginViewModel.toolbarUserLoginTitle}"/>
```

### 툴바를 구성하는 메서드를 구현한다.
[fragment/LoginFragment.kt]
```kt
    // 툴바를 구성하는 메서드
    fun settingToolbar(){
        fragmentLoginBinding.loginViewModel?.apply {
            toolbarUserLoginTitle.value = "로그인"
        }
    }
```

### 메서드를 호출한다.
[fragment/LoginFragment.kt - onCreateView()]
```kt
        // 툴바를 구성하는 메서드 호출
        settingToolbar()
```

--- 

# 06 회원 가입 화면 구성 1

### Fragment와 ViewModel을 만들어준다.
- UserJoinStep1Fragment
- UserJoinStep1ViewModel

### ViewModel의 코드를 작성한다.

[viewmodel/UserJoinStep1ViewModel.kt]
```kt
data class UserJoinStep1ViewModel(val userJoinStep1Fragment: UserJoinStep1Fragment) : ViewModel(){
}
```

### 화면을 구성해준다.

[res/layout/fragment_user_join_step1.xml]
```xml
<?xml version="1.0" encoding="utf-8"?>

<layout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <data>
        <variable
            name="userJoinStep1ViewModel"
            type="com.lion.boardproject.viewmodel.UserJoinStep1ViewModel" />
    </data>

    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context=".fragment.UserJoinStep1Fragment">

        <!-- TODO: Update blank fragment layout -->
        <TextView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:text="@string/hello_blank_fragment" />

    </FrameLayout>

</layout>

```

### Fragment의 기본 코드를 작성한다.

[fragment/UserJoinStep1Fragment.kt]
```kt
class UserJoinStep1Fragment : Fragment() {

    lateinit var fragmentUserJoinStep1Binding: FragmentUserJoinStep1Binding
    lateinit var userActivity: UserActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentUserJoinStep1Binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_join_step1, container, false)
        fragmentUserJoinStep1Binding.userJoinStep1ViewModel = UserJoinStep1ViewModel(this@UserJoinStep1Fragment)
        fragmentUserJoinStep1Binding.lifecycleOwner = this@UserJoinStep1Fragment

        userActivity = activity as UserActivity

        return fragmentUserJoinStep1Binding.root
    }

}
```

### Fragment의 이름을 정의한다.

[UserActivity.kt - UserFragmentName]
```kt
    // 회원 가입 화면
    USER_JOIN_STEP1_FRAGMENT(2, "UserJoinStep1Fragment"),
```

### Fragment의 객체를 생성한다.

[UserActivity.kt - replaceFragment()]
```kt
            // 회원 가입 과정1 화면
            UserFragmentName.USER_JOIN_STEP1_FRAGMENT -> UserJoinStep1Fragment()
```

### UserJoinStep1Fragment로 이동할 수 있는 메서드를 구현한다.
[fragment/LoginFragment.kt]
```kt
    // 회원 가입 화면으로 이동시키는 메서드
    fun moveToUserJoinStep1(){
        userActivity.replaceFragment(UserFragmentName.USER_JOIN_STEP1_FRAGMENT, true, true, null)
    }
```

### 버튼과 연결되는 메서드를 구현한다.
[viewmodel/LoginViewModel.kt]
```kt
    // buttonUserLoginJoin - onClick
    fun buttonUserLoginJoinOnClick(){
        loginFragment.moveToUserJoinStep1()
    }
```

### 버튼의 onclick 속성에 메서드 호출을 설정해준다.

[res/layout/fragment_login.xml]
```xml
    <Button
        ...
        android:onClick="@{(view) -> loginViewModel.buttonUserLoginJoinOnClick()}"/>
```

### 화면을 구성한다.
[res/layout/fragment_user_join_step1.xml]
```xml
<?xml version="1.0" encoding="utf-8"?>

<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <data>
        <variable
            name="userJoinStep1ViewModel"
            type="com.lion.boardproject.viewmodel.UserJoinStep1ViewModel" />
    </data>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        android:transitionGroup="true">

        <com.google.android.material.appbar.MaterialToolbar
            android:id="@+id/toolbarUserJoinStep1"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="@android:color/transparent"
            android:minHeight="?attr/actionBarSize"
            android:theme="?attr/actionBarTheme"/>

        <ScrollView
            android:layout_width="match_parent"
            android:layout_height="match_parent">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:padding="10dp" >

                <com.google.android.material.textfield.TextInputLayout
                    android:id="@+id/textFieldUserJoinStep1Id"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:hint="아이디"
                    app:endIconMode="clear_text"
                    app:startIconDrawable="@drawable/person_24px">

                    <com.google.android.material.textfield.TextInputEditText
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:digits="@string/digit_value"
                        android:singleLine="true"
                        android:textAppearance="@style/TextAppearance.AppCompat.Large" />
                </com.google.android.material.textfield.TextInputLayout>

                <com.google.android.material.textfield.TextInputLayout
                    android:id="@+id/textFieldUserJoinStep1Pw1"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:hint="비밀번호"
                    app:endIconMode="password_toggle"
                    app:startIconDrawable="@drawable/key_24px"
                    android:layout_marginTop="10dp">

                    <com.google.android.material.textfield.TextInputEditText
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:digits="@string/digit_value"
                        android:singleLine="true"
                        android:inputType="text|textPassword"
                        android:textAppearance="@style/TextAppearance.AppCompat.Large" />
                </com.google.android.material.textfield.TextInputLayout>

                <com.google.android.material.textfield.TextInputLayout
                    android:id="@+id/textFieldUserJoinStep1Pw2"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:hint="비밀번호 확인"
                    app:endIconMode="password_toggle"
                    app:startIconDrawable="@drawable/key_24px"
                    android:layout_marginTop="10dp">

                    <com.google.android.material.textfield.TextInputEditText
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:digits="@string/digit_value"
                        android:singleLine="true"
                        android:inputType="text|textPassword"
                        android:textAppearance="@style/TextAppearance.AppCompat.Large" />
                </com.google.android.material.textfield.TextInputLayout>

                <Button
                    android:id="@+id/buttonUserJoinStep1Next"
                    style="@style/Widget.Material3.Button.OutlinedButton"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="10dp"
                    android:text="다음"
                    android:textAppearance="@style/TextAppearance.AppCompat.Large" />

            </LinearLayout>
        </ScrollView>

    </LinearLayout>

</layout>

```

### ViewModel에 Live 데이터를 정의한다.

[viewmodel/userJoinStep1ViewModel.kt]
```kt
    // toolbarUserLogin - title
    val toolbarUserLoginTitle = MutableLiveData<String>()
    // toolbarUserLogin - navigationIcon
    val toolbarUserLoginNavigationIcon = MutableLiveData<Int>()
```

### Live 데이터와 UI 요소의 속성과 연결해준다.

[res/layout/fragment_user_join_step1.xml]
```xml
        <com.google.android.material.appbar.MaterialToolbar
            ...
            app:title="@{userJoinStep1ViewModel.toolbarUserLoginTitle}"
            app:navigationIcon="@{userJoinStep1ViewModel.toolbarUserLoginNavigationIcon}"/>
```

### Toolbar를 구성하는 메서드를 작성한다

[fragment/UserJoinStep1Fragment.kt]
```kt
    fun settingToolbar(){
        fragmentUserJoinStep1Binding.userJoinStep1ViewModel?.apply { 
            // 타이틀
            toolbarUserLoginTitle.value = "회원 가입"
            // 네비게이션 아이콘
            toolbarUserLoginNavigationIcon.value = R.drawable.arrow_back_24px
        }
    }
```

### 메서드를 호출한다.
[fragment/UserJoinStep1Fragment.kt - onCreateView()]
```kt
        // 툴바를 구성하는 메서드를 호출한다.
        settingToolbar()
```

### 네비게이션 아이콘을 클릭했을때 뒤로 가는 기능을 구현한다.
- toolbar의 속성에는 네비게이션 아이콘 클릭에 대한 리스너 속성이 없다.
- 이번 작업은 없는 속성을 만들어주는 작업을 해본다.

- build.gradle.kts 에 kept 설정을 해준다.
[build.gradle.kts]
```kt
plugins{
    ....
    kotlin("kapt")
} 
```

- ViewModel에 호출될 메서드를 작성해준다.

[viewmoddel/UserJoinStep1ViewModel.kt]
```kt
    companion object{
        // toolbarUserJoinStep1 - onNavigationClickUserJoinStep1
        @JvmStatic
        // xml 에 설정할 속성이름을 설정한다.
        @BindingAdapter("onNavigationClickUserJoinStep1")
        // 호출되는 메서드를 구현한다.
        // 첫 번째 매개변수 : 이 속성이 설정되어 있는 View 객체
        // 그 이후 : 전달해주는 값이 들어온다. xml 에서는 ViewModel이 가는 프로퍼티에 접근할 수 있기 때문에
        // 이것을 통해 Fragment객체를 받아 사용할것이다.
        fun onNavigationClickUserJoinStep1(materialToolbar:MaterialToolbar, userJoinStep1Fragment: UserJoinStep1Fragment){
            materialToolbar.setNavigationOnClickListener {
                // 이전으로 돌아간다.
                userJoinStep1Fragment.userActivity.removeFragment(UserFragmentName.USER_JOIN_STEP1_FRAGMENT)
            }
        }
    }
```

### layout에 해당 속성을 적용해준다.

[res/layout/fragment_user_join_step1.xml]
```xml
app:onNavigationClickUserJoinStep1="@{userJoinStep1ViewModel.userJoinStep1Fragment}"
```

---

# 07_회원 가입 단계 1의 입력 요소 처리

### 입력 요소와 연결할 MutableLiveData를 정의해준다.

[viewmodel/UserJoinStep1ViewModel.kt]
```kt
    // textFieldUserJoinStep1Id - EditText - text
    val textFieldUserJoinStep1IdEditTextText = MutableLiveData("")
    // textFieldUserJoinStep1Pw1 - EditText - text
    val textFieldUserJoinStep1Pw1EditTextText = MutableLiveData("")
    // textFieldUserJoinStep1Pw2 - EditText - text
    val textFieldUserJoinStep1Pw2EditTextText = MutableLiveData("")
```

### layout 파일에 LiveData를 설정해준다.

```xml
                <com.google.android.material.textfield.TextInputLayout
                    android:id="@+id/textFieldUserJoinStep1Id"
                    ...

                    <com.google.android.material.textfield.TextInputEditText
                        ...
                        android:text="@={userJoinStep1ViewModel.textFieldUserJoinStep1IdEditTextText}"/>
                </com.google.android.material.textfield.TextInputLayout>

                <com.google.android.material.textfield.TextInputLayout
                    android:id="@+id/textFieldUserJoinStep1Pw1"
                    ...

                    <com.google.android.material.textfield.TextInputEditText
                        ...
                        android:text="@={userJoinStep1ViewModel.textFieldUserJoinStep1Pw1EditTextText}"/>
                </com.google.android.material.textfield.TextInputLayout>

                <com.google.android.material.textfield.TextInputLayout
                    android:id="@+id/textFieldUserJoinStep1Pw2"
                    ...

                    <com.google.android.material.textfield.TextInputEditText
                        ...
                        android:text="@={userJoinStep1ViewModel.textFieldUserJoinStep1Pw2EditTextText}"/>
                </com.google.android.material.textfield.TextInputLayout>
```

### 키보드 관련 메서드를 넣어준다.
[UserActivity.kt]

```kt
// 키보드 올리는 메서드
fun showSoftInput(view: View){
    // 입력을 관리하는 매니저
    val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    // 포커스를 준다.
    view.requestFocus()

    thread {
        SystemClock.sleep(500)
        // 키보드를 올린다.
        inputManager.showSoftInput(view, 0)
    }
}
// 키보드를 내리는 메서드
fun hideSoftInput(){
    // 포커스가 있는 뷰가 있다면
    if(currentFocus != null){
        // 입력을 관리하는 매니저
        val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        // 키보드를 내린다.
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        // 포커스를 해제한다.
        currentFocus?.clearFocus()
    }
}
```

### Dialog를 띄우는 메서드를 구현한다.

[UserActivity.kt]
```kt
    // 다이얼로그를 통해 메시지를 보여주는 함수
    fun showMessageDialog(title:String, message:String, posTitle:String, callback:()-> Unit){
        val builder = MaterialAlertDialogBuilder(this@UserActivity)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(posTitle){ dialogInterface: DialogInterface, i: Int ->
            callback()
        }
        builder.show()
    }
```

### 버튼을 누르면 호출될 메서드를 구현해준다.

[viewmodel/UserJoinStep1ViewModel.kt]
```kt
    // buttonUserJoinStep1Next - onClick
    fun buttonUserJoinStep1NextOnClick(){
        userJoinStep1Fragment.apply {
            // 입력요소 검사
            if(textFieldUserJoinStep1IdEditTextText.value?.isEmpty()!!){
                userActivity.showMessageDialog("아이디 입력", "아이디를 입력해주세요", "확인"){
                    userActivity.showSoftInput(fragmentUserJoinStep1Binding.textFieldUserJoinStep1Id.editText!!)
                }
                return
            }
            if(textFieldUserJoinStep1Pw1EditTextText.value?.isEmpty()!!){
                userActivity.showMessageDialog("비밀번호 입력", "비밀번호를 입력해주세요", "확인"){
                    userActivity.showSoftInput(fragmentUserJoinStep1Binding.textFieldUserJoinStep1Pw1.editText!!)
                }
                return
            }
            if(textFieldUserJoinStep1Pw2EditTextText.value?.isEmpty()!!){
                userActivity.showMessageDialog("비밀번호 입력", "비밀번호를 입력해주세요", "확인"){
                    userActivity.showSoftInput(fragmentUserJoinStep1Binding.textFieldUserJoinStep1Pw2.editText!!)
                }
                return
            }
            if(textFieldUserJoinStep1Pw1EditTextText.value != textFieldUserJoinStep1Pw2EditTextText.value){
                userActivity.showMessageDialog("비밀번호 입력", "비밀번호가 다릅니다", "확인"){
                    textFieldUserJoinStep1Pw1EditTextText.value = ""
                    textFieldUserJoinStep1Pw2EditTextText.value = ""
                    userActivity.showSoftInput(fragmentUserJoinStep1Binding.textFieldUserJoinStep1Pw1.editText!!)
                }
                return
            }
        }
    }
```

### 메서드를 버튼의 onclick 속성과 연결해준다.

```xml
                <Button
                    ...
                    android:onClick="@{(view) -> userJoinStep1ViewModel.buttonUserJoinStep1NextOnClick()}"/>
```

