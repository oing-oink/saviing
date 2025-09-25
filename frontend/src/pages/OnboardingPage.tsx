import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import CustomCarousel, {
  type CarouselSlide,
} from '@/shared/components/common/CustomCarousel';
import { onboardingSlides } from '@/features/onboarding/data/onboardingData';
import { getGoogleOAuthUrl } from '@/features/auth/api/authApi';
import { useCustomerStore } from '@/features/auth/store/useCustomerStore';
import { PAGE_PATH } from '@/shared/constants/path';
import GoogleLoginButton from '@/features/auth/components/GoogleLoginButton';

const OnboardingPage = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useCustomerStore();

  // 이미 로그인된 사용자는 홈으로 리다이렉트
  useEffect(() => {
    if (isAuthenticated) {
      navigate(PAGE_PATH.HOME, { replace: true });
    }
  }, [isAuthenticated, navigate]);

  const handleLogin = () => {
    const googleOAuthUrl = getGoogleOAuthUrl();
    window.location.href = googleOAuthUrl; // 외부 OAuth URL은 window.location.href 사용
  };

  const renderOnboardingSlide = (slide: CarouselSlide) => {
    // 이미지 비율에 따른 클래스 설정
    const getImageClass = (id: string) => {
      if (id === 'intro_2') {
        // intro_2는 가로가 긴 이미지 - 슬라이드에 꽉 채우기
        return 'mb-4 h-48 w-full object-cover object-top rounded-lg';
      }
      if (id === 'intro_4') {
        // intro_4(2번째) 이미지를 가로로 꽉 채우기
        return 'mb-4 h-48 w-full object-cover object-top rounded-lg';
      }
      if (id === 'intro_3') {
        // intro_3(4번째) 이미지 - 아래쪽 더 많이 자르기
        return 'mb-4 h-48 w-full object-cover object-top rounded-lg';
      }
      // intro_1은 세로가 긴 이미지 - 아래쪽 더 많이 자르기
      return 'mb-4 h-48 w-full object-cover object-top rounded-lg';
    };

    return (
      <div className="flex h-96 flex-col items-center justify-center rounded-2xl bg-gradient-to-br from-blue-50 to-indigo-100 p-6 text-center">
        <img
          src={slide.image}
          alt={slide.title}
          className={getImageClass(slide.id)}
        />
        <h3 className="mb-2 text-lg font-bold text-gray-800">{slide.title}</h3>
        {slide.subtitle && (
          <p className="text-sm text-gray-600">{slide.subtitle}</p>
        )}
      </div>
    );
  };

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-gradient-to-b from-blue-50 via-white to-indigo-50 px-6">
      <div className="w-full max-w-md">
        {/* Header */}
        <div className="py-8 text-center">
          <h1 className="mb-2 text-3xl font-bold text-gray-800">saviing</h1>
          <p className="text-lg font-medium text-indigo-600">
            슬기로운 적금생활 가이드
          </p>
          <p className="mt-2 text-sm text-gray-600">
            지루한 적금을 유잼으로 전환!
          </p>
        </div>

        {/* Carousel */}
        <div className="mb-8">
          <CustomCarousel
            slides={onboardingSlides}
            autoplay={true}
            autoplayDelay={4000}
            showIndicators={true}
            renderSlide={renderOnboardingSlide}
          />
        </div>

        {/* Login Button */}
        <div className="pb-8">
          <GoogleLoginButton onClick={handleLogin} />
          <p className="mt-4 text-center text-xs text-gray-500">
            계속하면 서비스 이용약관과 개인정보처리방침에 동의하게 됩니다.
          </p>
        </div>
      </div>
    </div>
  );
};

export default OnboardingPage;
