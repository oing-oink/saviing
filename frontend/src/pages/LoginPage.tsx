import GoogleLoginButton from '@/features/auth/components/GoogleLoginButton';

const LoginPage = () => {
  const handleGoogleLogin = () => {
    // TODO: OAuth 2.0 구글 로그인 API 연동
    console.log('Google 로그인 클릭');
  };

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-gradient-to-b from-blue-50 to-white px-4">
      <div className="flex w-full max-w-sm flex-col items-center space-y-14">
        {/* Bankbook 이미지 */}
        <div>
          <img
            src="/onboarding/Bankbook.svg"
            alt="통장 이미지"
            className="h-auto w-64"
          />
        </div>

        {/* 로고 + 슬로건 묶음 */}
        <div className="flex flex-col items-center space-y-4">
          {/* 로고 */}
          <img
            src="/onboarding/Logo.svg"
            alt="Saving 로고"
            className="h-auto w-40"
          />

          {/* 슬로건 */}
          <h1 className="font-pretendard text-xl font-medium text-gray-700">
            저축으로 누리는 즐거움
          </h1>
        </div>

        {/* 구글 로그인 버튼 */}
        <GoogleLoginButton onClick={handleGoogleLogin} />
      </div>
    </div>
  );
};

export default LoginPage;
