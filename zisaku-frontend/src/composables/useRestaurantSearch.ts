import { ref, computed, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { apiService } from '../services/api';
import { type Restaurant } from '../stores/auth';

export function useRestaurantSearch() {
  const route = useRoute();
  const router = useRouter();

  // --- 状態管理 ---
  const allResults = ref<Restaurant[]>([]);
  const isLoading = ref(false);
  const isAndSearch = ref(false);
  const isOpen = ref(false); // アコーディオン用

  // ページネーション用
  const pageSize = ref(50);
  const currentPage = ref(0);
  const totalPages = ref(0);
  const totalElements = ref(0);
  const isFirstPage = ref(true);
  const isLastPage = ref(true);

  // マスタデータ用
  const restGenres = ref<string[]>([]);
  const restAreas = ref<string[]>([]);
  const restPriceRanges = ref<string[]>([]);
  const restRatings = ref<number[]>([]);

  // --- 検索入力フォーム用の同期リアクティブデータ ---
  // クエリパラメータ、または初期値（空文字）をセット
  const searchForm = ref({
    restaurantName: (route.query.restaurantName as string) || '',
    restaurantGenre: (route.query.restaurantGenre as string) || '',
    restaurantArea: (route.query.restaurantArea as string) || '',
    restaurantRating: (route.query.restaurantRating as string) || '',
    restaurantPriceRange: (route.query.restaurantPriceRange as string) || '',
  });

  // --- マスタデータ取得 ---
  const fetchMasterData = async () => {
    try {
      const [genres, areas, prices, ratings] = await Promise.all([
        apiService.getgenres(),
        apiService.getAreas(),
        apiService.getPriceRanges(),
        apiService.getRatings(),
      ]);
      restGenres.value = genres.data;
      restAreas.value = areas.data;
      restPriceRanges.value = prices.data;
      restRatings.value = ratings.data;
    } catch (error) {
      console.error('マスタデータの取得に失敗しました:', error);
    }
  };

  // --- 検索実行 (API通信) ---
  const executeSearch = async (page = 0) => {
    isLoading.value = true;
    currentPage.value = page;

    try {
      const searchParams = {
        restaurantName: (route.query.restaurantName as string) || '',
        restaurantGenre: (route.query.restaurantGenre as string) || '',
        restaurantArea: (route.query.restaurantArea as string) || '',
        restaurantRating: (route.query.restaurantRating as string) || '',
        restaurantPriceRange: (route.query.restaurantPriceRange as string) || '',
        isAndSearch: isAndSearch.value,
        page: page,
        size: pageSize.value,
        sort: (route.query.sort as string) || 'restaurantId,asc',
      };

      const response = await apiService.searchRestaurants(searchParams);

      allResults.value = response.data.content;
      totalPages.value = response.data.page.totalPages;
      totalElements.value = response.data.page.totalElements;
      isFirstPage.value = response.data.page.number === 0;
      isLastPage.value = response.data.page.number >= response.data.page.totalPages - 1;
    } catch (error) {
      console.error('検索エラー:', error);
      allResults.value = [];
      totalPages.value = 0;
      totalElements.value = 0;
      isFirstPage.value = true;
      isLastPage.value = true;
    } finally {
      isLoading.value = false;
    }
  };

  // --- 検索ボタン押下時の遷移処理 ---
  const handleSearchSubmit = () => {
    // 空文字の条件を除外してクエリを作成
    const activeConditions = Object.fromEntries(
      Object.entries(searchForm.value).filter(([_, value]) => value !== '')
    );
    // 検索モード（AND/OR）もクエリに含めて遷移する
    activeConditions.isAndSearch = isAndSearch.value ? 'true' : 'false';
    router.push({ path: '/result', query: activeConditions });
  };

  // --- ソート変更ハンドラ ---
  const handleSortChange = (event: Event) => {
    const target = event.target as HTMLSelectElement;
    router.push({
      query: {
        ...route.query,
        sort: target.value,
        page: 0,
      },
    });
  };

  // --- 条件クリア ---
  const clearQuery = () => {
    searchForm.value = {
      restaurantName: '',
      restaurantGenre: '',
      restaurantArea: '',
      restaurantRating: '',
      restaurantPriceRange: '',
    };
  };

  // --- トグル系関数 ---
  const toggleRating = (value: string) => {
    searchForm.value.restaurantRating =
      searchForm.value.restaurantRating === value ? '' : value;
  };

  const togglePrice = (value: string) => {
    searchForm.value.restaurantPriceRange =
      searchForm.value.restaurantPriceRange === value ? '' : value;
  };

  const toggleAccordion = () => {
    isOpen.value = !isOpen.value;
  };

  const toggleSearchMode = () => {
    isAndSearch.value = !isAndSearch.value;
  }

  // --- 算出プロパティ ---
  const noResult = computed(() => !isLoading.value && totalElements.value === 0);

  // クエリの変更を監視してフォームに同期（ブラウザの「戻る」対策）
  watch(
    () => route.query,
    (newQuery) => {
      searchForm.value = {
        restaurantName: (newQuery.restaurantName as string) || '',
        restaurantGenre: (newQuery.restaurantGenre as string) || '',
        restaurantArea: (newQuery.restaurantArea as string) || '',
        restaurantRating: (newQuery.restaurantRating as string) || '',
        restaurantPriceRange: (newQuery.restaurantPriceRange as string) || '',
      };
      // URLクエリにisAndSearchがあれば同期する（ブラウザ操作での一貫性確保）
      isAndSearch.value = (newQuery.isAndSearch as string) === 'true';
    },
    { deep: true }
  );

  return {
    // 状態
    allResults,
    isLoading,
    isAndSearch,
    isOpen,
    searchForm,
    // ページネーション
    pageSize,
    currentPage,
    totalPages,
    totalElements,
    isFirstPage,
    isLastPage,
    noResult,
    // マスタデータ
    restGenres,
    restAreas,
    restPriceRanges,
    restRatings,
    // 関数
    fetchMasterData,
    executeSearch,
    handleSearchSubmit,
    handleSortChange,
    clearQuery,
    toggleRating,
    togglePrice,
    toggleAccordion,
    toggleSearchMode,
  };
}