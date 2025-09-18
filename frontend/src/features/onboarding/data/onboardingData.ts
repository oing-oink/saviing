export interface OnboardingSlide {
  id: string;
  title: string;
  subtitle?: string;
  image: string;
}

export const onboardingSlides: OnboardingSlide[] = [
  {
    id: 'intro_1',
    title: '저축과 게임을 연결',
    subtitle: '펫과 적금 현황을 한 눈에 볼 수 있어요!',
    image: '/onboarding/intro_1.png',
  },
  {
    id: 'intro_4',
    title: '쉽고 직관적인 적금 관리',
    subtitle: '가입부터 현황 조회, 해지까지 간편하게 할 수 있어요!',
    image: '/onboarding/intro_4.png',
  },
  {
    id: 'intro_2',
    title: '적금으로 펫을 키워봐요',
    subtitle: '먹이주기와 놀아주기로 펫도 키우고 이자율도 얻어요!',
    image: '/onboarding/intro_2.png',
  },
  {
    id: 'intro_3',
    title: '나만의 펫이 지내는 공간을 꾸밀 수도 있어요!',
    image: '/onboarding/intro_3.png',
  },
];
