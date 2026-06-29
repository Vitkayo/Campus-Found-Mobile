export interface Item {
  id: number;
  title: string;
  category: string;
  status: 'Lost' | 'Found' | 'Claimed';
  location: string;
  date: string;
  time: string;
  image: string | null;
  description: string;
  contact: string;
  reporter: { name: string; initials: string };
}

export const CATEGORIES = [
  'Electronics', 'Wallet', 'Student ID', 'Keys', 'Books', 'Clothing', 'Other',
];

export const LOCATIONS = [
  'IFL Building Lobby, RUPP',
  'Central Library, RUPP',
  'Computer Lab, Faculty of Engineering, RUPP',
  'Faculty of Science, RUPP',
  'Faculty of Social Sciences, RUPP',
  'Administration Building, RUPP',
  'RUPP Sports Center',
  'RUPP Cafeteria',
  'Main Gate, RUPP',
  'Parking Lot, RUPP',
];

export const mockItems: Item[] = [
  {
    id: 1,
    title: 'Student ID Card',
    category: 'Student ID',
    status: 'Lost',
    location: 'IFL Building Lobby, RUPP',
    date: 'Jun 20, 2026',
    time: '12:18 PM',
    image: null,
    description:
      'A student ID card found near the IFL Building entrance. Belongs to SREYNEATH (ID: IDKHM2503651158). The card was handed to the faculty secretary. Please contact to claim.',
    contact: '4564864',
    reporter: { name: 'Sreyneath Pov', initials: 'SP' },
  },
  {
    id: 2,
    title: 'Samsung Phone',
    category: 'Electronics',
    status: 'Lost',
    location: 'Central Library, RUPP',
    date: 'Jun 20, 2026',
    time: '12:17 PM',
    image: null,
    description:
      'Lost my Samsung phone on the 2nd floor reading area of the central library. It has a matte black protective case with a small scratch on the bottom-left corner.',
    contact: '012345678',
    reporter: { name: 'Dara Mao', initials: 'DM' },
  },
  {
    id: 3,
    title: 'MacBook Air M2 Silver',
    category: 'Electronics',
    status: 'Lost',
    location: 'Computer Lab, Faculty of Engineering, RUPP',
    date: 'Jun 13, 2026',
    time: '3:03 PM',
    image: null,
    description:
      'Left my MacBook Air M2 (Silver, 256 GB) in the computer lab. Has a distinctive red dragon sticker on the lid and "KVK" written in black marker on the underside.',
    contact: '098765432',
    reporter: { name: 'Vuthy Keo', initials: 'VK' },
  },
  {
    id: 4,
    title: 'Samsung Galaxy S23',
    category: 'Electronics',
    status: 'Found',
    location: 'Central Library, RUPP',
    date: 'Jun 13, 2026',
    time: '3:03 PM',
    image: null,
    description:
      'Found a Samsung Galaxy S23 (cream/white) near the library main entrance. Screen has a hairline crack on the top-right corner. Currently held at the library front desk.',
    contact: '011234567',
    reporter: { name: 'Flow Tester', initials: 'FT' },
  },
  {
    id: 5,
    title: 'Blue Leather Wallet',
    category: 'Wallet',
    status: 'Found',
    location: 'RUPP Cafeteria',
    date: 'Jun 12, 2026',
    time: '11:30 AM',
    image: null,
    description:
      'Found a slim blue leather wallet near the cafeteria seating area. Contains some cash, a Wing bank card, and a student ID. Owner please contact me immediately.',
    contact: '017890123',
    reporter: { name: 'Mony Chan', initials: 'MC' },
  },
  {
    id: 6,
    title: 'Key Ring — 3 Keys',
    category: 'Keys',
    status: 'Found',
    location: 'Main Gate, RUPP',
    date: 'Jun 11, 2026',
    time: '2:45 PM',
    image: null,
    description:
      'Found a metal key ring with 3 keys and a small rubber duck keychain (blue) near the main entrance security post. Left with the security officer.',
    contact: '096789012',
    reporter: { name: 'Ratha Nhem', initials: 'RN' },
  },
  {
    id: 7,
    title: 'Calculus Textbook',
    category: 'Books',
    status: 'Lost',
    location: 'Faculty of Science, RUPP',
    date: 'Jun 10, 2026',
    time: '9:00 AM',
    image: null,
    description:
      'Lost my Calculus textbook (James Stewart, 8th edition) in the science faculty hallway. Name "Sophea" written on the first page in blue pen.',
    contact: '015678901',
    reporter: { name: 'Sophea Ly', initials: 'SL' },
  },
];

export const currentUser = {
  name: 'Flow Tester',
  email: 'flow@university.edu',
  phone: '098765432',
  initials: 'FT',
  posts: mockItems.filter(i => i.reporter.name === 'Flow Tester'),
};
