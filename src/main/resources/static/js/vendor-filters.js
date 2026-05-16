/**
 * Shared vendor category and Sri Lanka district lists.
 * Keep in sync with vendor registration (vendor-setup.html).
 */
const VENDOR_CATEGORIES = [
  'Photography',
  'Jewellery and Attire',
  'Florist',
  'Gifts',
  'Wedding music and dancing',
  'Food and catering'
];

const SRI_LANKA_DISTRICTS = [
  'Ampara',
  'Anuradhapura',
  'Badulla',
  'Batticaloa',
  'Colombo',
  'Galle',
  'Gampaha',
  'Hambantota',
  'Jaffna',
  'Kalutara',
  'Kandy',
  'Kegalle',
  'Kilinochchi',
  'Kurunegala',
  'Mannar',
  'Matale',
  'Matara',
  'Moneragala',
  'Mullaitivu',
  'Nuwara Eliya',
  'Polonnaruwa',
  'Puttalam',
  'Ratnapura',
  'Trincomalee',
  'Vavuniya'
];

function populateCategorySelect(selectEl, options = {}) {
  if (!selectEl) return;
  const { allLabel = 'All categories', allValue = '' } = options;
  selectEl.innerHTML = '';
  if (allLabel != null) {
    const allOpt = document.createElement('option');
    allOpt.value = allValue;
    allOpt.textContent = allLabel;
    selectEl.appendChild(allOpt);
  }
  VENDOR_CATEGORIES.forEach((category) => {
    const opt = document.createElement('option');
    opt.value = category;
    opt.textContent = category;
    selectEl.appendChild(opt);
  });
}

function populateDistrictSelect(selectEl, options = {}) {
  if (!selectEl) return;
  const { allLabel = 'All districts', allValue = '' } = options;
  selectEl.innerHTML = '';
  if (allLabel != null) {
    const allOpt = document.createElement('option');
    allOpt.value = allValue;
    allOpt.textContent = allLabel;
    selectEl.appendChild(allOpt);
  }
  SRI_LANKA_DISTRICTS.forEach((district) => {
    const opt = document.createElement('option');
    opt.value = district;
    opt.textContent = district;
    selectEl.appendChild(opt);
  });
}

function matchesVendorFilters(vendor, filters = {}) {
  const { category, location } = filters;
  if (category && vendor.category !== category) return false;
  if (location && vendor.location !== location) return false;
  return true;
}

function getVendorFilterParams(search = window.location.search) {
  const params = new URLSearchParams(search);
  const category = params.get('category') || '';
  const location = params.get('location') || '';
  return {
    category: category && category !== 'All categories' ? category : '',
    location: location && location !== 'All districts' && location !== 'All locations' ? location : ''
  };
}

function buildVendorFilterQuery(filters = {}) {
  const params = new URLSearchParams();
  if (filters.category) params.set('category', filters.category);
  if (filters.location) params.set('location', filters.location);
  const query = params.toString();
  return query ? `?${query}` : '';
}
