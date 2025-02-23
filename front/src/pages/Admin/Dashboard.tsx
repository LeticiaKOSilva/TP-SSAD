import { ChangeEvent, FormEvent, useEffect, useState } from 'react';
import { AlertTriangle, Upload, Plus } from 'lucide-react';
import { DEFAULT_PRODUCT_IMAGE, Product } from '../../types';
import useAuth from '../../context/useAuthContext';
import * as XLSX from 'xlsx';

export default function Dashboard() {
  const { user } = useAuth();
  const [products, setProducts] = useState<Product[]>([]);
  const [showAddProduct, setShowAddProduct] = useState(false);
  const [newProduct, setNewProduct] = useState<Partial<Product>>({});
  const [editingProductId, setEditingProductId] = useState<string | null>(null);

  const lowStockProducts = products.filter(product => product.quantidadeEstoque <= product.estoqueMinimo);

  useEffect(() => {
    if (user) {
      fetchProducts();
    }
  }, [user]);

  const fetchProducts = async () => {
    try {
      const products = await getProducts();
      setProducts(
        products.map((product) => ({
          ...product,
          urlImagem:
            product.urlImagem ||
            DEFAULT_PRODUCT_IMAGE,
        })) as Product[]
      );
    } catch (error) {
      console.error(error);
      alert('Erro ao buscar produtos');
    }
  };

  if (!user) return

  const getProducts = async () => {
    const resp = await fetch('/api/produto/', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });
  
    if (!resp.ok) {
      const error = await resp.text();
      throw new Error(error);
    }
  
    return await resp.json();
  }

  const handleAddProduct = async (e: FormEvent) => {
    e.preventDefault();
    if (newProduct.nome && newProduct.preco && newProduct.quantidadeEstoque) {
      try {
        let product: Product;
        if (editingProductId) {
          product = await updateProduct(newProduct as Product);
  
          setProducts(
            products.map((p) => (p.id === editingProductId ? product : p))
          );
        } else {
          product = await createProduct(newProduct as Product);
          setProducts([
            ...products,
            {
              ...product,
              urlImagem:
                product.urlImagem ||
                'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&q=80&w=800',
            } as Product,
          ]);
        }
  
        setShowAddProduct(false);
        setNewProduct({});
        setEditingProductId(null);
      } catch (error) {
        console.error(error);
        alert('Erro ao criar/atualizar produto');
      }
    }
  };  

  const createProduct = async (product: Product) => {
    const resp = await fetch('/api/produto/create', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ login: user.pessoa.email, senha: user.senha, produto: product}),
    });
    
    if (!resp.ok) {
      const error = await resp.text();
      throw new Error(error);
    }

    return await resp.json();
  }

  const updateProduct = async (product: Product) => {
    const resp = await fetch(
      `/api/produto/update`,
      {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          login: user.pessoa.email,
          senha: user.senha,
          produto: product,
        }),
      }
    );
  
    if (!resp.ok) {
      const error = await resp.text();
      throw new Error(error);
    }
  
    return await resp.json();
  };

  const handleFileUpload = async (e: ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
  
    const reader = new FileReader();
    reader.onload = async (event) => {
      const data = event.target?.result;
      if (data) {
        const workbook = XLSX.read(data, { type: 'binary' });
        const sheetName = workbook.SheetNames[0];
        const worksheet = workbook.Sheets[sheetName];
        const json = XLSX.utils.sheet_to_json(worksheet, { header: 1 });
  
        if (json.length > 0) {
          const headers = (json[0] as string[]).map(header =>
            header
              .toLowerCase() // Converte para minúsculas
              .replace(/\s+/g, '') // Remove espacos em branco
              .replace(/_/g, '') // Remove underline
          );
  
          // eslint-disable-next-line @typescript-eslint/no-explicit-any
          const products: Partial<Product>[] = json.slice(1).map((row: any) => {
            const product: Partial<Product> = {};
            headers.forEach((header, index) => {
              const value = row[index];
              switch (header) {
                case 'nome':
                  product.nome = value;
                  break;
                case 'descricao':
                case 'descricão':
                  product.descricao = value;
                  break;
                case 'preco':
                case 'preço':
                  product.preco = parseFloat(value);
                  break;
                case 'quantidadeemestoque':
                  product.quantidadeEstoque = parseInt(value, 10);
                  break;
                case 'estoqueminimo':
                case 'estoquemínimo':
                  product.estoqueMinimo = parseInt(value, 10);
                  break;
                case 'urldaimagem':
                case 'urlimagem':
                  product.urlImagem = value;
                  break;
                default:
                  break;
              }
            });
            return product;
          });
  
          try {
            await Promise.all(products.map(product => createProduct(product as Product)));
            alert('Produtos importados com sucesso!');
            fetchProducts();
          } catch (error) {
            console.error(error);
            alert('Erro ao importar produtos');
          }
        }
      }
    };
  
    reader.readAsBinaryString(file);
  };

  return (
    <div className="min-h-screen pt-24 pb-12">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col gap-2 sm:flex-row sm:justify-between sm:items-center mb-8">
          <h2 className="text-3xl font-bold text-gray-900">Dashboard</h2>
          <div className="flex flex-col sm:flex-row gap-2">
            <label className="inline-flex w-fit items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 cursor-pointer">
              <Upload className="h-4 w-4 mr-2" />
              Importar Produtos
              <input
                type="file"
                accept=".csv,.xlsx"
                className="hidden"
                onChange={handleFileUpload}
              />
            </label>
            <button
              onClick={() => setShowAddProduct(true)}
              className="w-fit inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-green-600 hover:bg-green-700"
            >
              <Plus className="h-4 w-4 mr-2" />
              Novo Produto
            </button>
          </div>
        </div>

        {lowStockProducts.length > 0 && (
          <div className="mb-8 p-4 bg-yellow-50 border-l-4 border-yellow-400 rounded-md">
            <div className="flex items-center">
              <AlertTriangle className="h-5 w-5 text-yellow-400 mr-2" />
              <h3 className="text-lg font-medium text-yellow-800">
                Produtos com Estoque Baixo
              </h3>
            </div>
            <div className="mt-2">
              <ul className="list-disc list-inside">
                {lowStockProducts.map(product => (
                  <li key={product.id} className="text-yellow-700">
                    {product.nome} - {product.quantidadeEstoque} unidades restantes
                  </li>
                ))}
              </ul>
            </div>
          </div>
        )}

        <div className="bg-white shadow rounded-lg overflow-hidden">
          <div className="block md:hidden">
            {products.map(product => (
              <div key={product.id} className="p-4 border-b border-gray-200">
                <div className="flex items-center">
                  <img
                    src={product.urlImagem}
                    alt={product.nome}
                    className="h-10 w-10 rounded-full object-cover"
                  />
                  <div className="ml-4">
                    <div className="text-sm font-medium text-gray-900">
                      {product.nome}
                    </div>
                    <div className="text-sm text-gray-500">
                      {product.descricao}
                    </div>
                  </div>
                </div>
                <div className="mt-2">
                  <div className="text-sm text-gray-900">
                    R$ {product.preco.toFixed(2)}
                  </div>
                  <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                    product.quantidadeEstoque <= 3
                      ? 'bg-red-100 text-red-800'
                      : 'bg-green-100 text-green-800'
                  }`}>
                    {product.quantidadeEstoque} unidades
                  </span>
                </div>
                <div className="mt-2">
                  <button className="text-indigo-600 hover:text-indigo-900"
                    onClick={() => {
                      setEditingProductId(product.id!);
                      setNewProduct(product);
                      setShowAddProduct(true);
                    }}
                  >
                    Editar
                  </button>
                </div>
              </div>
            ))}
          </div>
          <table className="min-w-full divide-y divide-gray-200 hidden md:table">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Produto
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Preço
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Estoque
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Ações
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {products.map(product => (
                <tr key={product.id}>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
                      <img
                        src={product.urlImagem}
                        alt={product.nome}
                        className="h-10 w-10 rounded-full object-cover"
                      />
                      <div className="ml-4">
                        <div className="text-sm font-medium text-gray-900">
                          {product.nome}
                        </div>
                        <div className="text-sm text-gray-500">
                          {product.descricao}
                        </div>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm text-gray-900">
                      R$ {product.preco.toFixed(2).replace('.', ',')}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                      product.quantidadeEstoque <= 3
                        ? 'bg-red-100 text-red-800'
                        : 'bg-green-100 text-green-800'
                    }`}>
                      {product.quantidadeEstoque} unidades
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    <button
                      className="text-indigo-600 hover:text-indigo-900"
                      onClick={() => {
                        setEditingProductId(product.id!);
                        setNewProduct(product);
                        setShowAddProduct(true);
                      }}
                    >
                      Editar
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {
          showAddProduct && (
            <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
              <div className="bg-white rounded-lg p-8 max-w-md w-full">
                <h3 className="text-lg font-medium text-gray-900 mb-6">
                  {editingProductId ? 'Editar Produto' : 'Novo Produto'}
                </h3>
                <form onSubmit={handleAddProduct} className="space-y-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700">
                      Nome
                    </label>
                    <input
                      type="text"
                      required
                      value={newProduct.nome || ''}
                      onChange={(e) =>
                        setNewProduct({ ...newProduct, nome: e.target.value })
                      }
                      className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700">
                      Descrição
                    </label>
                    <textarea
                      value={newProduct.descricao || ''}
                      onChange={(e) =>
                        setNewProduct({ ...newProduct, descricao: e.target.value })
                      }
                      className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700">
                      Preço
                    </label>
                    <input
                      type="number"
                      step="0.01"
                      required
                      value={newProduct.preco || ''}
                      onChange={(e) =>
                        setNewProduct({
                          ...newProduct,
                          preco: parseFloat(e.target.value),
                        })
                      }
                      className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700">
                      Quantidade em Estoque
                    </label>
                    <input
                      type="number"
                      required
                      value={newProduct.quantidadeEstoque || ''}
                      onChange={(e) =>
                        setNewProduct({
                          ...newProduct,
                          quantidadeEstoque: parseInt(e.target.value),
                        })
                      }
                      className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700">
                      Estoque Mínimo
                    </label>
                    <input
                      type="number"
                      required
                      value={newProduct.estoqueMinimo || ''}
                      onChange={(e) =>
                        setNewProduct({
                          ...newProduct,
                          estoqueMinimo: parseInt(e.target.value),
                        })
                      }
                      className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700">
                      URL da Imagem
                    </label>
                    <input
                      type="url"
                      value={newProduct.urlImagem || ''}
                      onChange={(e) =>
                        setNewProduct({ ...newProduct, urlImagem: e.target.value })
                      }
                      className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2"
                    />
                  </div>
                  <div className="flex justify-end space-x-4 mt-6">
                    <button
                      type="button"
                      onClick={() => {
                        setShowAddProduct(false);
                        setEditingProductId(null);
                        setNewProduct({});
                      }}
                      className="px-4 py-2 text-sm font-medium text-gray-700 hover:text-gray-500"
                    >
                      Cancelar
                    </button>
                    <button
                      type="submit"
                      className="px-4 py-2 text-sm font-medium text-white bg-indigo-600 rounded-md hover:bg-indigo-700"
                    >
                      {editingProductId ? 'Atualizar' : 'Salvar'}
                    </button>
                  </div>
                </form>
              </div>
            </div>
          )
        }
      </div>
    </div>
  );
}