const AuthCallbackPage = () => {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-gradient-to-b from-blue-50 to-white">
      <div className="flex flex-col items-center space-y-4">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-blue-500 border-t-transparent"></div>
        <p className="font-pretendard text-lg text-gray-700">
          로그인 처리중...
        </p>
      </div>
    </div>
  );
};

export default AuthCallbackPage;
